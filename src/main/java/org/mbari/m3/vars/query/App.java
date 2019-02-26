package org.mbari.m3.vars.query;

import com.google.common.base.Preconditions;
import com.guigarage.sdk.Application;
import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import org.mbari.m3.vars.query.messages.*;
import org.mbari.m3.vars.query.old.GlobalStateLookup;
import org.mbari.m3.vars.query.util.StateLookup;
import org.mbari.m3.vars.query.model.beans.QueryParams;
import org.mbari.m3.vars.query.model.beans.ResolvedConceptSelection;
import org.mbari.m3.vars.query.model.beans.ResultsCustomization;
import org.mbari.m3.vars.query.ui.AppIcons;
import org.mbari.m3.vars.query.controllers.SaveResultsController;
import org.mbari.m3.vars.query.ui.db.ConceptConstraint;
import org.mbari.m3.vars.query.ui.javafx.application.ImageFX;
import org.mbari.m3.vars.query.shared.rx.messages.AbstractExceptionMsg;
import org.mbari.m3.vars.query.shared.rx.messages.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.m3.vars.query.ui.sdkfx.AdvancedSearchWorkbench;
import org.mbari.m3.vars.query.ui.sdkfx.BasicSearchWorkbench;
import org.mbari.m3.vars.query.ui.sdkfx.ConceptConstraintsWorkbench;
import org.mbari.m3.vars.query.ui.sdkfx.ConceptMedia;
import org.mbari.m3.vars.query.ui.sdkfx.CustomizeResultsWorkbench;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 *
 */
public class App {

    /**
     * 378720000 = 1982-01-01
     */
    public static final Date MIN_RECORDED_DATE = new Date(378720000L * 1000L);
    private static Logger log;
    private final UIToolBox toolBox;
    private final EventBus eventBus;
    private Application application;
    private final AppController appController;
    private final SaveResultsController saveResultsController;
    private BasicSearchWorkbench basicSearchWorkbench;
    private ConceptConstraintsWorkbench conceptConstraintsWorkbench;
    private CustomizeResultsWorkbench customizeResultsWorkbench;
    private AdvancedSearchWorkbench advancedSearchWorkbench;

    public App(UIToolBox toolBox) {
        Preconditions.checkArgument(toolBox != null);
        this.eventBus = toolBox.getEventBus();
        this.appController = new AppController(toolBox);
        this.saveResultsController = new SaveResultsController(eventBus, toolBox.getExecutor());
        this.toolBox = toolBox;

        eventBus.toObserverable()
                .ofType(NewResolvedConceptSelectionMsg.class)
                .subscribe(msg -> addResolvedConceptSelection(msg.getResolvedConceptSelection()));

        eventBus.toObserverable()
                .ofType(AbstractExceptionMsg.class)
                .subscribe(msg -> log.error(msg.getMessage(), msg.getException()));

        eventBus.toObserverable()
                .ofType(ShowAdvancedSearchWorkbenchMsg.class)
                .subscribe(msg -> showAdvancedSearch(getApplication()));

        eventBus.toObserverable()
                .ofType(ShowBasicSearchWorkbenchMsg.class)
                .subscribe(msg -> showBasicSearch(getApplication()));

        eventBus.toObserverable()
                .ofType(ShowConceptConstraintsWorkbenchMsg.class)
                .subscribe(msg -> showConceptConstraintsWorkBench(getApplication()));

        eventBus.toObserverable()
                .ofType(ShowCustomizeResultsWorkbenchMsg.class)
                .subscribe(msg -> showCustomizeResults(getApplication()));
    }

    protected Application getApplication() {
        if (application == null) {
            application = new Application();
            ResourceBundle i18n = toolBox.getI18nBundle();
            application.addStylesheet(getClass().getResource("/org/mbari/m3/vars/query/queryfx/queryfx.css").toExternalForm());

            application.setTitle(i18n.getString("app.title"));
            String home = i18n.getString("app.toolbar.home");
            application.addToolbarItem(new Action(AppIcons.HOME, home, () -> eventBus.send(new ShowBasicSearchWorkbenchMsg())));

            application.setBaseColor(new Color(0x1B / 255D, 0x4D / 255D, 0x93 / 255D, 1));
            application.addMenuEntry(new Action(AppIcons.HOME, home,
                    () -> eventBus.send(new ShowBasicSearchWorkbenchMsg())));

            application.addMenuEntry(new Action(AppIcons.SEARCH_PLUS,
                    i18n.getString("app.menu.refine"),
                    () -> eventBus.send(new ShowAdvancedSearchWorkbenchMsg())));

            application.addMenuEntry(new Action(AppIcons.GEARS,
                    i18n.getString("app.menu.customize"),
                    () -> eventBus.send(new ShowCustomizeResultsWorkbenchMsg())));

            showBasicSearch(application);
            getAdvancedSearchWorkbench(); // If we don't call this returns are not initialized on the first query
        }
        return application;
    }

    protected void doSearch() {
        final List<ConceptConstraint> conceptConstraints = getBasicSearchWorkbench().getConceptSelections().stream()
                .map(ConceptConstraint::new)
                .collect(Collectors.toList());
        final QueryParams queryParams = getAdvancedSearchWorkbench().getQueryParams();
        if (conceptConstraints.isEmpty() && queryParams.getQueryConstraints().isEmpty()) {
            // Show warning dialog
            ResourceBundle i18n = toolBox.getI18nBundle();
            ShowAlert msg = new ShowInfoAlert(i18n.getString("search.alert.noconstraints.title"),
                    i18n.getString("search.alert.noconstraints.header"),
                    i18n.getString("search.alert.noconstraints.content"));
            eventBus.send(msg);
        }
        else {
            final ResultsCustomization resultsCustomization = getCustomizeResultsWorkbench().getResultsCustomization();
            Msg msg = new ExecuteSearchMsg(conceptConstraints, queryParams.getQueryReturns(), queryParams.getQueryConstraints(), resultsCustomization);
            eventBus.send(msg);
        }
    }

    protected void showBasicSearch(Application app) {
        WorkbenchView view = getBasicSearchWorkbench();
        app.setWorkbench(view);
        app.clearGlobalActions();
        app.addGlobalAction(new Action(AppIcons.SEARCH, this::doSearch));
    }

    protected void showAdvancedSearch(Application app) {
        app.clearGlobalActions();
        WorkbenchView view = getAdvancedSearchWorkbench();
        app.setWorkbench(view);
    }

    protected void showConceptConstraintsWorkBench(Application app) {
        app.clearGlobalActions();
        WorkbenchView view = getConceptConstraintsWorkbench(app);
        app.setWorkbench(view);
    }

    protected void showCustomizeResults(Application app) {
        app.clearGlobalActions();
        WorkbenchView view = getCustomizeResultsWorkbench();
        app.setWorkbench(view);
    }

    protected BasicSearchWorkbench getBasicSearchWorkbench()  {
        if (basicSearchWorkbench == null) {
            basicSearchWorkbench = new BasicSearchWorkbench(eventBus);
        }
        return basicSearchWorkbench;
    }

    protected ConceptConstraintsWorkbench getConceptConstraintsWorkbench(Application app) {
        if (conceptConstraintsWorkbench == null) {
            conceptConstraintsWorkbench = new ConceptConstraintsWorkbench(
                    toolBox.getQueryService(), toolBox.getExecutor(), eventBus);
//            conceptConstraintsWorkbench.getFormLayout().addActions(new Action(AppIcons.TRASH, "Cancel", () -> showBasicSearch(app)),
//                    new Action(AppIcons.PLUS, "Apply", () -> {
//                        ConceptSelection conceptSelection = conceptConstraintsWorkbench.getConceptSelection();
//                        eventBus.send(new NewConceptSelectionMsg(conceptSelection));
//                        showBasicSearch(app);
//                    }));
        }
        return conceptConstraintsWorkbench;
    }

    protected CustomizeResultsWorkbench getCustomizeResultsWorkbench() {
        if (customizeResultsWorkbench == null) {
            customizeResultsWorkbench = new CustomizeResultsWorkbench();
        }
        return customizeResultsWorkbench;
    }

    protected AdvancedSearchWorkbench getAdvancedSearchWorkbench() {
        if (advancedSearchWorkbench == null) {
            advancedSearchWorkbench = new AdvancedSearchWorkbench(toolBox.getQueryService(), eventBus);
        }
        return advancedSearchWorkbench;
    }

    private void addResolvedConceptSelection(ResolvedConceptSelection rcs) {

        Platform.runLater(() -> {
            BasicSearchWorkbench bsw = getBasicSearchWorkbench();
            ConceptMedia conceptMedia = new ConceptMedia(rcs);
            bsw.getConceptMedia().add(conceptMedia);
        });
    }



    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to store dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(App.class);
        }
        return log;
    }

    public static void main( String[] args ) {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        GlobalStateLookup.getSettingsDirectory(); // Not used

        /*
          Log uncaught Exceptions
         */
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            Logger log = LoggerFactory.getLogger(thread.getClass());
            log.error("Exception in thread [" + thread.getName() + "]", e);
        });

        final Logger mainLog = getLog();

        if (mainLog.isInfoEnabled()) {
            final Date date = new Date();
            mainLog.info("This application was launched at " + date.toString());
        }

        UIToolBox toolBox = Initializer.getToolBox();

        // Check that we can connect to the database. JDBC driver doesn't seem to get initialize
        // correctly in Java Webstart unless we do this.
        // TODO (20150712 brian) after switch away from ForkJoinPool this may no longer be needed
//        try {
//            Connection connection = toolBelt.getQueryService().getAnnotationConnection();
//            connection.close();
//        }
//        catch (SQLException e) {
//            getLog().error("Failed to connect to annotation database", e);
//        }


        App app = new App(toolBox);
        StateLookup.setApp(app);
        ImageFX.setIsJavaFXRunning(true);
        app.getApplication().setPrefSize(800, 900);
        app.getApplication().setStopCallback(() -> System.exit(0));
        app.getApplication().show();

    }

}
