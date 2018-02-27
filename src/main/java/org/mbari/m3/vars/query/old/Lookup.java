package org.mbari.m3.vars.query.old;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.App;
import org.mbari.m3.vars.query.model.ILink;
import org.mbari.m3.vars.query.model.LinkBean;
import org.mbari.m3.vars.query.old.services.knowledgebase.Concept;
import org.mbari.m3.vars.query.old.services.knowledgebase.SimpleConceptBean;
import org.mbari.m3.vars.query.old.services.knowledgebase.SimpleConceptNameBean;
import org.mbari.m3.vars.query.old.services.knowledgebase.ConceptNameTypes;
import org.mbari.m3.vars.query.util.QueryModule;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * @author Brian Schlining
 * @since 2015-07-17T16:47:00
 * @deprecated
 */
public class Lookup extends GlobalLookup {


    private static final String appConfig = "vars/queryfx/app";

    public static final String WILD_CARD = "*";
    public static final Concept WILD_CARD_CONCEPT = new SimpleConceptBean(
            new SimpleConceptNameBean(WILD_CARD, ConceptNameTypes.PRIMARY.getName()));
    public static final ILink WILD_CARD_LINK = new LinkBean(WILD_CARD, WILD_CARD, WILD_CARD);


    private static Injector injector;
    private static App app;
    private static Config config = ConfigFactory.load(appConfig);
    private static final Object injectorLock = new Object() {};
    private static final Object configLock = new Object() {};

    public static Injector getInjector() {
        synchronized (injectorLock) {
            if (injector == null) {
                injector = Guice.createInjector(new QueryModule());
            }
        }
        return injector;
    }


    public static App getApp() {
        return app;
    }

    public static void setApp(App app) {
        Preconditions.checkArgument(app != null);
        Lookup.app = app;
    }

    public static Config getConfig() {
        synchronized (configLock) {
            if (config == null) {
                config = ConfigFactory.load(appConfig);
            }
        }
        return config;
    }

    public static ZonedDateTime getAnnotationStartDate() {
        Config config = getConfig();
        String startDate = config.getString("vars.annotation.start.date");
        return ZonedDateTime.ofInstant(Instant.parse(startDate), ZoneId.of("UTC"));
    }


}
