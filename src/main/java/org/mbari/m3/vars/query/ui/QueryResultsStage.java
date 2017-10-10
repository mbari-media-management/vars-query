package org.mbari.m3.vars.query.ui;

import javafx.stage.Stage;
import org.mbari.m3.vars.query.services.query.results.QueryResults;


/**
 * @author Brian Schlining
 * @since 2015-07-31T14:19:00
 */
public class QueryResultsStage extends Stage {

    private final QueryResults queryResults;

    public QueryResultsStage(QueryResults queryResults) {
        this.queryResults = queryResults;
    }
}
