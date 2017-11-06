package org.mbari.m3.vars.query.messages;

import javafx.stage.Stage;
import org.mbari.m3.vars.query.old.services.query.results.QueryResults;
import org.mbari.m3.vars.query.shared.rx.messages.Msg;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-31T14:05:00
 */
public class NewQueryResultsMsg implements Msg {

    private final Stage stage;
    private final QueryResults queryResults;
    private final Optional<String> sql;

    public NewQueryResultsMsg(Stage stage, QueryResults queryResults, Optional<String> sql) {
        this.stage = stage;
        this.queryResults = queryResults;
        this.sql = sql;
    }

    public NewQueryResultsMsg(Stage stage, QueryResults queryResults) {
        this(stage, queryResults, Optional.empty());
    }

    public QueryResults getQueryResults() {
        return queryResults;
    }

    public Stage getStage() {
        return stage;
    }

    public Optional<String> getSql() {
        return sql;
    }
}
