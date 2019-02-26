package org.mbari.m3.vars.query.messages;

/**
 * @author Brian Schlining
 * @since 2019-02-26T09:49:00
 */
public class ShowExceptionAlert extends ShowAlert {

    private final Exception exception;

    public ShowExceptionAlert(String title, String headerText, String contentText, Exception exception) {
        super(title, headerText, contentText);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
