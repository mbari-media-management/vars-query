package org.mbari.m3.vars.query.util;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.App;
import org.mbari.m3.vars.query.old.GlobalStateLookup;
import org.mbari.m3.vars.query.model.ILink;
import org.mbari.m3.vars.query.old.services.knowledgebase.Concept;
import org.mbari.m3.vars.query.old.services.knowledgebase.SimpleConceptBean;
import org.mbari.m3.vars.query.old.services.knowledgebase.SimpleConceptNameBean;
import org.mbari.m3.vars.query.model.LinkBean;
import org.mbari.m3.vars.query.old.services.knowledgebase.ConceptNameTypes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Brian Schlining
 * @since 2016-03-30T10:10:00
 */
public class StateLookup extends GlobalStateLookup {

    private static final String appConfig = "vars/queryfx/app";
    private static Injector injector;
    private static final Config config = ConfigFactory.load(appConfig);
    private static App app;
    public static final String WILD_CARD = "*";
    public static final Concept WILD_CARD_CONCEPT = new SimpleConceptBean(
            new SimpleConceptNameBean(WILD_CARD, ConceptNameTypes.PRIMARY.getName()));
    public static final ILink WILD_CARD_LINK = new LinkBean(WILD_CARD, WILD_CARD, WILD_CARD);
    private static final Object lockObject = new Object();


    public static Config getConfig() {
        return config;
    }

    public static Injector getInjector() {
        if (injector == null) {
            synchronized (lockObject) {
                injector = Guice.createInjector(new QueryModule());
            }
        }
        return injector;
    }

    public static ZonedDateTime getAnnotationStartDate() {
        Config config = getConfig();
        String startDate = config.getString("vars.annotation.start.date");
        return ZonedDateTime.ofInstant(Instant.parse(startDate), ZoneId.of("UTC"));
    }

    public static App getApp() {
        return app;
    }

    public static void setApp(App app) {
        Preconditions.checkArgument(app != null);
        StateLookup.app = app;
    }


}
