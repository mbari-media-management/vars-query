package org.mbari.m3.vars.query;

import io.reactivex.Observable;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.mbari.m3.vars.query.messages.*;
import org.mbari.m3.vars.query.controllers.QueryResultsUIController;
import org.mbari.m3.vars.query.ui.Alerts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.m3.vars.query.util.VARSException;
import org.mbari.m3.vars.query.old.services.query.results.AssociationColumnRemappingDecorator;
import org.mbari.m3.vars.query.old.services.query.results.CoalescingDecorator;
import org.mbari.m3.vars.query.old.services.query.results.QueryResults;
import org.mbari.m3.vars.query.services.AsyncQueryService;
import org.mbari.m3.vars.query.model.beans.ConceptSelection;
import org.mbari.m3.vars.query.model.beans.ResultsCustomization;
import org.mbari.m3.vars.query.ui.db.ConceptConstraint;
import org.mbari.m3.vars.query.ui.db.IConstraint;
import org.mbari.m3.vars.query.ui.db.PreparedStatementGenerator;
import org.mbari.m3.vars.query.ui.db.SQLStatementGenerator;
import org.mbari.m3.vars.query.ui.db.results.QueryResultsDecorator;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Brian Schlining
 * @since 2015-07-26T11:29:00
 */
public class AppController {

    private final AsyncQueryService queryService;
    private final EventBus eventBus;
    private final Executor executor;
    private final QueryResultsUIController uiController;
    private final Alerts alerts;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public AppController(UIToolBox toolBox) {
        this.queryService = toolBox.getQueryService();
        this.eventBus = toolBox.getEventBus();
        this.executor = toolBox.getExecutor();
        this.alerts = new Alerts(toolBox);
        this.uiController = new QueryResultsUIController(eventBus);

        Observable<Object> observable = eventBus.toObserverable();

        observable.ofType(NewConceptSelectionMsg.class)
                .subscribe(msg -> addConceptSelection(msg.getConceptSelection()));

        observable.ofType(ExecuteSearchMsg.class)
                .subscribe(this::executeSearch);

        observable.ofType(ShowAlert.class)
                .subscribe(alerts::showAlert);

    }

    protected void addConceptSelection(ConceptSelection conceptSelection) {

        CompletableFuture<Optional<URL>> urlF = queryService.resolveImageURL(conceptSelection.getConceptName());

        queryService.resolveConceptSelection(conceptSelection)
                .thenCombineAsync(urlF, (rcs, urlOpt) -> {
                    if (urlOpt.isPresent()) {
                        Platform.runLater(() -> {
                            Image image = new Image(urlOpt.get().toExternalForm(), true);
                            rcs.imageProperty().set(image);
                        });
                    }
                    return rcs;

                })
                .thenAccept(rcs -> eventBus.send(new NewResolvedConceptSelectionMsg(rcs)));

    }

    protected void executeSearch(ExecuteSearchMsg msg) {

        SQLStatementGenerator sqlGen = new SQLStatementGenerator();
        String sql = sqlGen.getSQLStatement(msg.getQueryReturns(),
                msg.getConceptConstraints(),
                msg.getQueryConstraints(),
                msg.getResultsCustomization());
        log.debug("Executing: " + sql);
        System.out.println(sql);

        CompletableFuture<Stage> stageF = uiController.newQueryStage();

        CompletableFuture<QueryResults> queryResultsF = runQuery(msg.getQueryReturns(),
                msg.getConceptConstraints(),
                msg.getQueryConstraints(),
                msg.getResultsCustomization());

        stageF.thenAcceptBothAsync(queryResultsF, (stage, queryResults) ->
                eventBus.send(new NewQueryResultsMsg(stage, queryResults, Optional.of(sql))), executor);

    }

    public CompletableFuture<QueryResults> runQuery(List<String> queryReturns,
            List<ConceptConstraint> conceptConstraints,
            List<IConstraint> queryConstraints,
            ResultsCustomization resultsCustomization) {

        return CompletableFuture.supplyAsync(() -> {

            PreparedStatementGenerator psg = new PreparedStatementGenerator();

            try {
                String template = psg.getPreparedStatementTemplate(queryReturns,
                        conceptConstraints,
                        queryConstraints,
                        resultsCustomization);

                if (log.isDebugEnabled()) {
                    log.debug("PreparedStatement Template: " + template);
                }
                Connection connection = queryService.getAnnotationConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(template);
                psg.bind(preparedStatement, conceptConstraints, queryConstraints);
                QueryResults queryResults = QueryResults.fromResultSet(preparedStatement.executeQuery());

                if (resultsCustomization.isCategorizeAssociations()) {
                    queryResults = AssociationColumnRemappingDecorator.apply(queryResults);
                }

                queryResults = CoalescingDecorator.coalesce(queryResults, "observation_uuid");

                QueryResultsDecorator queryResultsDecorator = new QueryResultsDecorator(queryService);
                if (resultsCustomization.isConceptHierarchy()) {
                    queryResults = queryResultsDecorator.addHierarchy(queryResults);
                }

                if (resultsCustomization.isDetailedPhylogeny()) {
                    queryResults = queryResultsDecorator.addFullPhylogeny(queryResults);
                }
                else if (resultsCustomization.isBasicPhylogeny()) {
                    queryResults = queryResultsDecorator.addBasicPhylogeny(queryResults);
                }

                return queryResults;

            }
            catch (SQLException e) {
                throw new VARSException("Failed to execute prepared statement", e);
            }
        }, executor);

    }

}
