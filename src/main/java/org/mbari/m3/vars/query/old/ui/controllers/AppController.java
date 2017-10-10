package org.mbari.m3.vars.query.old.ui.controllers;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.mbari.m3.vars.query.old.messages.NewResolvedConceptSelectionMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.m3.vars.query.old.util.VARSException;
import org.mbari.m3.vars.query.old.services.query.results.AssociationColumnRemappingDecorator;
import org.mbari.m3.vars.query.old.services.query.results.CoalescingDecorator;
import org.mbari.m3.vars.query.old.services.query.results.QueryResults;
import org.mbari.m3.vars.query.old.services.AsyncQueryService;
import org.mbari.m3.vars.query.old.shared.rx.RXEventBus;
import org.mbari.m3.vars.query.old.model.beans.ConceptSelection;
import org.mbari.m3.vars.query.old.model.beans.ResultsCustomization;
import org.mbari.m3.vars.query.old.messages.ExecuteSearchMsg;
import org.mbari.m3.vars.query.old.messages.NewConceptSelectionMsg;
import org.mbari.m3.vars.query.old.messages.NewQueryResultsMsg;
import org.mbari.m3.vars.query.old.ui.db.ConceptConstraint;
import org.mbari.m3.vars.query.old.ui.db.IConstraint;
import org.mbari.m3.vars.query.old.ui.db.PreparedStatementGenerator;
import org.mbari.m3.vars.query.old.ui.db.SQLStatementGenerator;
import org.mbari.m3.vars.query.old.ui.db.results.QueryResultsDecorator;

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
    private final RXEventBus eventBus;
    private final Executor executor;
    private final QueryResultsUIController uiController;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public AppController(AsyncQueryService queryService, RXEventBus eventBus, Executor executor) {
        this.queryService = queryService;
        this.eventBus = eventBus;
        this.executor = executor;
        this.uiController = new QueryResultsUIController(eventBus);


        eventBus.toObserverable()
                .filter(msg -> msg instanceof NewConceptSelectionMsg)
                .map(msg -> (NewConceptSelectionMsg) msg)
                .subscribe(msg -> addConceptSelection(msg.getConceptSelection()));

        eventBus.toObserverable()
                .filter(msg -> msg instanceof ExecuteSearchMsg)
                .map(msg -> (ExecuteSearchMsg) msg)
                .subscribe(this::executeSearch);

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

                queryResults = CoalescingDecorator.coalesce(queryResults, "ObservationID_FK");

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
