package org.mbari.m3.vars.query;

import com.typesafe.config.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * @author Brian Schlining
 * @since 2017-11-02T13:20:00
 */
public class UIToolBox {
    private final EventBus eventBus;
    private final ResourceBundle i18nBundle;
    private final Config config;
    private final Services services;

    /** URL to the stylesheet used for the apps */
    private final Collection<String> stylesheets;

    public UIToolBox(Services services, EventBus eventBus, ResourceBundle i18nBundle,
                     Config config, Collection<String> stylesheets) {
        this.eventBus = eventBus;
        this.i18nBundle = i18nBundle;
        this.config = config;
        this.services = services;
        this.stylesheets = Collections.unmodifiableCollection(stylesheets);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ResourceBundle getI18nBundle() {
        return i18nBundle;
    }

    public Config getConfig() {
        return config;
    }

    public Services getServices() {
        return services;
    }

    public Collection<String> getStylesheets() {
        return stylesheets;
    }
}
