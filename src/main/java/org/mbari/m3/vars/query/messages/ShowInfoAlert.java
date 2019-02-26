package org.mbari.m3.vars.query.messages;

/**
 * @author Brian Schlining
 * @since 2019-02-26T09:52:00
 */
public class ShowInfoAlert extends ShowAlert {
    public ShowInfoAlert(String title, String headerText, String contentText) {
        super(title, headerText, contentText);
    }
}
