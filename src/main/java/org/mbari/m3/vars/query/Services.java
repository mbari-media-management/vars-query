package org.mbari.m3.vars.query;

import org.mbari.m3.vars.query.services.AnnotationService;
import org.mbari.m3.vars.query.services.ConceptService;

import javax.inject.Inject;

/**
 * @author Brian Schlining
 * @since 2017-11-02T13:21:00
 */
public class Services {

    private final AnnotationService annotationService;
    private final ConceptService conceptService;

    @Inject
    public Services(AnnotationService annotationService, ConceptService conceptService) {
        this.annotationService = annotationService;
        this.conceptService = conceptService;
    }

    public AnnotationService getAnnotationService() {
        return annotationService;
    }

    public ConceptService getConceptService() {
        return conceptService;
    }
}
