package org.mbari.m3.vars.query.old.shared.rx.messages;

/**
 * @author Brian Schlining
 * @since 2015-07-19T13:38:00
 */
public class FatalExceptionMsg extends AbstractExceptionMsg  {


    public FatalExceptionMsg(String msg, Throwable exception) {
        super(msg, exception);
    }

}
