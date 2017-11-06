package org.mbari.m3.vars.query;

import com.typesafe.config.Config;
import org.mbari.m3.vars.query.services.AsyncQueryService;

import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

/**
 * @author Brian Schlining
 * @since 2017-11-02T13:20:00
 */
public class UIToolBox {
    private final EventBus eventBus;
    private final ResourceBundle i18nBundle;
    private final Config config;
    private final AsyncQueryService queryService;
    private final Executor executor;

    /** URL to the stylesheet used for the apps */
    private final Collection<String> stylesheets;


    public UIToolBox(AsyncQueryService queryService,
                     EventBus eventBus,
                     ResourceBundle i18nBundle,
                     Config config,
                     Collection<String> stylesheets,
                     Executor executor) {
        this.queryService = queryService;
        this.eventBus = eventBus;
        this.i18nBundle = i18nBundle;
        this.config = config;
        this.stylesheets = Collections.unmodifiableCollection(stylesheets);
        this.executor = executor;
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

    public Collection<String> getStylesheets() {
        return stylesheets;
    }

    public AsyncQueryService getQueryService() {
        return queryService;
    }

    public Executor getExecutor() {
        return executor;
    }
}
