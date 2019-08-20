package org.mbari.m3.vars.query;

import com.typesafe.config.Config;
import org.mbari.m3.vars.query.services.AnnotationService;
import org.mbari.m3.vars.query.services.AsyncQueryService;
import org.mbari.m3.vars.query.services.ConceptService;
import org.mbari.m3.vars.query.services.M3AsyncQueryServiceImpl;
import org.mbari.m3.vars.query.services.annosaurus.jdbc.AnnotationServiceImpl;
import org.mbari.m3.vars.query.services.varskbserver.v1.KBConceptService;
import org.mbari.m3.vars.query.services.varskbserver.v1.KBWebServiceFactory;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Brian Schlining
 * @since 2019-08-20T11:53:00
 */
public class ServicesBuilder {

    private final AppConfig appConfig;
    private final Executor executor;

    private ServicesBuilder(Config config, Executor executor) {
        this.appConfig = new AppConfig(config);
        this.executor = executor;
    }

    public static Services build(Config config, Executor executor) {
        return new ServicesBuilder(config, executor).build();
    }

    public static Services build(Config config) {
        return build(config, new ForkJoinPool());
    }

    private Services build() {
        ConceptService conceptService = buildConceptService();
        AnnotationService annotationService = buildAnnotationService();
        AsyncQueryService asyncQueryService = new M3AsyncQueryServiceImpl(annotationService, conceptService);
        return new Services(conceptService, annotationService, asyncQueryService);
    }

    private ConceptService buildConceptService() {
        URL endpoint = appConfig.getConceptServiceUrl();
        Duration timeout = appConfig.getConceptServiceTimeout();
        KBWebServiceFactory factory = new KBWebServiceFactory(endpoint.toExternalForm(),
                timeout,
                executor);
        return new KBConceptService(factory);
    }

    private AnnotationService buildAnnotationService() {
        return new AnnotationServiceImpl(appConfig.getAnnosaurusJdbcUrl(),
                appConfig.getAnnosaurusJdbcUser(),
                appConfig.getAnnosaurusJdbcPassword(),
                appConfig.getAnnosaurusJdbcDriver());
    }

}
