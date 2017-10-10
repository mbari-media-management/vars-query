package org.mbari.m3.vars.query.old.messages;

import org.mbari.m3.vars.query.old.services.query.results.QueryResults;
import org.mbari.m3.vars.query.old.shared.rx.messages.Msg;

import java.io.File;

/**
 * @author Brian Schlining
 * @since 2016-04-11T13:59:00
 */
public class SaveAsJSONMsg implements Msg {

    private final File target;
    private final QueryResults queryResults;

    public SaveAsJSONMsg(File target, QueryResults queryResults) {
        this.queryResults = queryResults;
        this.target = target;
    }

    public QueryResults getQueryResults() {
        return queryResults;
    }

    public File getTarget() {
        return target;
    }
}
