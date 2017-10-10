package org.mbari.m3.vars.query.old;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.guigarage.sdk.Application;
import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import org.mbari.m3.vars.query.old.util.StateLookup;
import org.mbari.m3.vars.query.old.util.ToolBelt;
import org.mbari.m3.vars.query.old.model.beans.QueryParams;
import org.mbari.m3.vars.query.old.model.beans.ResolvedConceptSelection;
import org.mbari.m3.vars.query.old.model.beans.ResultsCustomization;
import org.mbari.m3.vars.query.old.ui.AppIcons;
import org.mbari.m3.vars.query.old.ui.controllers.AppController;
import org.mbari.m3.vars.query.old.ui.controllers.SaveResultsController;
import org.mbari.m3.vars.query.old.ui.db.ConceptConstraint;
import org.mbari.m3.vars.query.old.ui.javafx.application.ImageFX;
import org.mbari.m3.vars.query.old.shared.rx.RXEventBus;
import org.mbari.m3.vars.query.old.shared.rx.messages.AbstractExceptionMsg;
import org.mbari.m3.vars.query.old.shared.rx.messages.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.m3.vars.query.old.messages.ShowAdvancedSearchWorkbenchMsg;
import org.mbari.m3.vars.query.old.messages.ShowBasicSearchWorkbenchMsg;
import org.mbari.m3.vars.query.old.messages.ShowConceptConstraintsWorkbenchMsg;
import org.mbari.m3.vars.query.old.messages.ShowCustomizeResultsWorkbenchMsg;
import org.mbari.m3.vars.query.old.messages.ExecuteSearchMsg;
import org.mbari.m3.vars.query.old.messages.NewResolvedConceptSelectionMsg;
import org.mbari.m3.vars.query.old.ui.sdkfx.AdvancedSearchWorkbench;
import org.mbari.m3.vars.query.old.ui.sdkfx.BasicSearchWorkbench;
import org.mbari.m3.vars.query.old.ui.sdkfx.ConceptConstraintsWorkbench;
import org.mbari.m3.vars.query.old.ui.sdkfx.ConceptMedia;
import org.mbari.m3.vars.query.old.ui.sdkfx.CustomizeResultsWorkbench;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
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
    private RXEventBus eventBus = new RXEventBus();
    private static Logger log;
    private final ToolBelt toolBelt;
    private Application application;
    private final AppController appController;
    private final SaveResultsController saveResultsController;
    private BasicSearchWorkbench basicSearchWorkbench;
    private ConceptConstraintsWorkbench conceptConstraintsWorkbench;
    private CustomizeResultsWorkbench customizeResultsWorkbench;
    private AdvancedSearchWorkbench advancedSearchWorkbench;

    public App(ToolBelt toolBelt) {
        Preconditions.checkArgument(toolBelt != null);
        this.appController = new AppController(toolBelt.getQueryService(), eventBus, toolBelt.getExecutor());
        this.saveResultsController = new SaveResultsController(eventBus, toolBelt.getExecutor());
        this.toolBelt = toolBelt;

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
            application.addStylesheet(getClass().getResource("/org/mbari/m3/vars/query/queryfx/queryfx.css").toExternalForm());

            application.setTitle("VARS Query");
            application.addToolbarItem(new Action(AppIcons.HOME, "Home", () -> eventBus.send(new ShowBasicSearchWorkbenchMsg())));

            application.setBaseColor(new Color(0x1B / 255D, 0x4D / 255D, 0x93 / 255D, 1));
            application.addMenuEntry(new Action(AppIcons.HOME, "Home",
                    () -> eventBus.send(new ShowBasicSearchWorkbenchMsg())));

            application.addMenuEntry(new Action(AppIcons.SEARCH_PLUS, "Refine Search",
                    () -> eventBus.send(new ShowAdvancedSearchWorkbenchMsg())));

            application.addMenuEntry(new Action(AppIcons.GEARS, "Customize Results",
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Whoops!");
            alert.setHeaderText("Unable to complete search");
            alert.setContentText("You did not specify any search constraints, so you are asking for the entire database." +
                    " That's not allowed");
            alert.showAndWait();
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
                    toolBelt.getQueryService(), toolBelt.getExecutor(), eventBus);
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
            advancedSearchWorkbench = new AdvancedSearchWorkbench(toolBelt.getQueryService(), eventBus);
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
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable e) {
                Logger log = LoggerFactory.getLogger(thread.getClass());
                log.error("Exception in thread [" + thread.getName() + "]", e);
            }
        });

        final Logger mainLog = getLog();

        if (mainLog.isInfoEnabled()) {
            final Date date = new Date();
            mainLog.info("This application was launched at " + date.toString());
        }

        Injector injector = StateLookup.getInjector();
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);

        // Check that we can connect to the database. JDBC driver doesn't seem to get initialize
        // correctly in Java Webstart unless we do this.
        // TODO (20150712 brian) after switch away from ForkJoinPool this may no longer be needed
        try {
            Connection connection = toolBelt.getQueryService().getAnnotationConnection();
            connection.close();
        }
        catch (SQLException e) {
            getLog().error("Failed to connect to annotation database", e);
        }


        App app = new App(toolBelt);
        StateLookup.setApp(app);
        ImageFX.setIsJavaFXRunning(true);
        app.getApplication().setPrefSize(800, 900);
        app.getApplication().setStopCallback(() -> System.exit(0));
        app.getApplication().show();

    }

}
