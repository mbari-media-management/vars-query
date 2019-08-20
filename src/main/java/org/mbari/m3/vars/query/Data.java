package org.mbari.m3.vars.query;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Brian Schlining
 * @since 2019-08-20T15:12:00
 */
public class Data {

    private final ObjectProperty<App> app = new SimpleObjectProperty<>();

    public App getApp() {
        return app.get();
    }

    public ObjectProperty<App> appProperty() {
        return app;
    }

    public void setApp(App app) {
        this.app.set(app);
    }
}
