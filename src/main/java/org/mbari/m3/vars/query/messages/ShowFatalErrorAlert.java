package org.mbari.m3.vars.query.messages;

/**
 * @author Brian Schlining
 * @since 2019-02-26T09:52:00
 */
public class ShowFatalErrorAlert extends ShowExceptionAlert {

    public ShowFatalErrorAlert(String title, String headerText, String contentText, Exception exception) {
        super(title, headerText, contentText, exception);
    }
}