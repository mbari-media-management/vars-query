package org.mbari.m3.vars.query.old.util;

import com.google.inject.Binder;
import com.google.inject.Module;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.BuilderFactory;
import org.mbari.m3.vars.query.old.Resource;
import org.mbari.m3.vars.query.old.services.AsyncQueryService;
import org.mbari.m3.vars.query.old.services.AsyncQueryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.m3.vars.query.old.services.jpa.VarsJpaModule;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author Brian Schlining
 * @since 2015-07-19T13:05:00
 */
public class QueryModule implements Module {

    public final Logger log = LoggerFactory.getLogger(getClass());

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;


    public QueryModule() {
        Resource resource = new Resource(StateLookup.getConfig());
        log.info(resource.getConfig().toString());
        annotationPersistenceUnit = resource.findByKey("vars.annotation.persistence.unit").get();
        knowledgebasePersistenceUnit = resource.findByKey("vars.knowledgebase.persistence.unit").get();
        miscPersistenceUnit = resource.findByKey("vars.misc.persistence.unit").get();
    }

    public void configure(Binder binder) {
        binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
        binder.bind(BuilderFactory.class).to(JavaFXBuilderFactory.class);
        binder.bind(AsyncQueryService.class).to(AsyncQueryServiceImpl.class).asEagerSingleton();

        // Fork join pool causes problems in java web start
        //binder.bind(Executor.class).to(ForkJoinPool.class).asEagerSingleton();
        Executor executor = Executors.newCachedThreadPool();
        binder.bind(Executor.class).toInstance(executor);
    }
}
