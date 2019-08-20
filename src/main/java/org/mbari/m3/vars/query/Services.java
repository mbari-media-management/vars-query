package org.mbari.m3.vars.query;

import org.mbari.m3.vars.query.services.AnnotationService;
import org.mbari.m3.vars.query.services.AsyncQueryService;
import org.mbari.m3.vars.query.services.ConceptService;

/**
 * @author Brian Schlining
 * @since 2019-08-20T11:50:00
 */
public class Services {

    private final ConceptService conceptService;
    private final AnnotationService annotationService;
    private final AsyncQueryService asyncQueryService;

    public Services(ConceptService conceptService,
                    AnnotationService annotationService,
                    AsyncQueryService asyncQueryService) {
        this.conceptService = conceptService;
        this.annotationService = annotationService;
        this.asyncQueryService = asyncQueryService;
    }

    public ConceptService getConceptService() {
        return conceptService;
    }

    public AnnotationService getAnnotationService() {
        return annotationService;
    }

    public AsyncQueryService getAsyncQueryService() {
        return asyncQueryService;
    }
}
