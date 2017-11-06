package org.mbari.m3.vars.query;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.services.AnnotationService;
import org.mbari.m3.vars.query.services.AsyncQueryService;
import org.mbari.m3.vars.query.services.ConceptService;
import org.mbari.m3.vars.query.services.M3AsyncQueryServiceImpl;
import org.mbari.m3.vars.query.services.annosaurus.jdbc.AnnotationServiceImpl;
import org.mbari.m3.vars.query.services.varskbserver.v1.KBConceptService;
import org.mbari.m3.vars.query.services.varskbserver.v1.KBWebServiceFactory;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Brian Schlining
 * @since 2017-11-02T15:56:00
 */
public class MBARIInjectorModule implements Module {


    private final Config config;
    private final Executor defaultExecutor = new ForkJoinPool();

    public MBARIInjectorModule() {
        this.config = ConfigFactory.load();
    }

    @Override
    public void configure(Binder binder) {
        configureConceptService(binder);
        binder.bind(AnnotationService.class).toInstance(new AnnotationServiceImpl());
        binder.bind(AsyncQueryService.class).to(M3AsyncQueryServiceImpl.class);
    }

    private void configureConceptService(Binder binder) {
        String endpoint = config.getString("concept.service.url");
        Duration timeout = config.getDuration("concept.service.timeout");
        KBWebServiceFactory factory = new KBWebServiceFactory(endpoint, timeout,
                defaultExecutor);
        KBConceptService service = new KBConceptService(factory);
        binder.bind(String.class)
                .annotatedWith(Names.named("CONCEPT_ENDPOINT"))
                .toInstance(endpoint);
        binder.bind(KBWebServiceFactory.class).toInstance(factory);
        binder.bind(ConceptService.class).toInstance(service);
    }
}
