package org.mbari.m3.vars.query.messages;

/**
 * @author Brian Schlining
 * @since 2019-02-26T09:53:00
 */
public class ShowNonfatalErrorAlert extends ShowExceptionAlert {

    public ShowNonfatalErrorAlert(String title, String headerText, String contentText, Exception exception) {
        super(title, headerText, contentText, exception);
    }

}
