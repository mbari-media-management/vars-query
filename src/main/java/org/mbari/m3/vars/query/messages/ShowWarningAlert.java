package org.mbari.m3.vars.query.messages;

/**
 * @author Brian Schlining
 * @since 2019-02-26T09:53:00
 */
public class ShowWarningAlert extends ShowAlert {

    public ShowWarningAlert(String title, String headerText, String contentText) {
        super(title, headerText, contentText);
    }
}
