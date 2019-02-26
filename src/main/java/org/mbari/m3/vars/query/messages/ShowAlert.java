package org.mbari.m3.vars.query.messages;

/**
 * @author Brian Schlining
 * @since 2019-02-26T09:49:00
 */
public abstract class ShowAlert {
    private final String title;
    private final String headerText;
    private final String contentText;

    ShowAlert(String title, String headerText, String contentText) {
        this.title = title;
        this.headerText = headerText;
        this.contentText = contentText;
    }


    public String getTitle() {
        return title;
    }

    public String getHeaderText() {
        return headerText;
    }

    public String getContentText() {
        return contentText;
    }

}
