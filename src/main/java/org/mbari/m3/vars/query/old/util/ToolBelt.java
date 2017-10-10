package org.mbari.m3.vars.query.old.util;

import org.mbari.m3.vars.query.old.services.AsyncQueryService;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationDAOFactory;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationFactory;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationPersistenceService;
import org.mbari.m3.vars.query.old.services.knowledgebase.KnowledgebaseDAOFactory;
import org.mbari.m3.vars.query.old.services.misc.MiscDAOFactory;
import org.mbari.m3.vars.query.old.services.misc.MiscFactory;
import org.mbari.m3.vars.query.old.services.knowledgebase.KnowledgebaseFactory;
import org.mbari.m3.vars.query.old.services.query.QueryPersistenceService;

import com.google.inject.Inject;

import java.util.concurrent.Executor;

public class ToolBelt extends org.mbari.m3.vars.query.old.ToolBelt {

    private final AsyncQueryService queryService;
    private final Executor executor;

    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory,
                    AnnotationFactory annotationFactory,
                    KnowledgebaseDAOFactory knowledgebaseDAOFactory,
                    KnowledgebaseFactory knowledgebaseFactory,
                    MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
                    PersistenceCacheProvider persistenceCacheProvider,
                    AnnotationPersistenceService annotationPersistenceService,
                    QueryPersistenceService queryPersistenceService,
                    AsyncQueryService queryService,
                    Executor executor) {
        super(annotationDAOFactory, annotationFactory, knowledgebaseDAOFactory,
                knowledgebaseFactory, miscDAOFactory, miscFactory,
                persistenceCacheProvider, annotationPersistenceService,
                queryPersistenceService);
        this.queryService = queryService;
        this.executor = executor;
    }

    public AsyncQueryService getQueryService() {
        return queryService;
    }

    public Executor getExecutor() {
        return executor;
    }
}