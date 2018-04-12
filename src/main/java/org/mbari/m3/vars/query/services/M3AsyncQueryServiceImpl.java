package org.mbari.m3.vars.query.services;

import org.mbari.m3.vars.query.model.Concept;
import org.mbari.m3.vars.query.model.ConceptDetails;
import org.mbari.m3.vars.query.model.ConceptMedia;
import org.mbari.m3.vars.query.model.ILink;
import org.mbari.m3.vars.query.model.beans.ConceptSelection;
import org.mbari.m3.vars.query.model.beans.ResolvedConceptSelection;
import org.mbari.m3.vars.query.services.varskbserver.v1.HierarchyDecorator;

import javax.inject.Inject;
import javax.swing.text.html.Option;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2017-11-03T08:22:00
 */
public class M3AsyncQueryServiceImpl implements AsyncQueryService {

    private final AnnotationService annotationService;
    private final ConceptService conceptService;
    private final HierarchyDecorator hierarchyDecorator;


    @Inject
    public M3AsyncQueryServiceImpl(AnnotationService annotationService, ConceptService conceptService) {
        this.annotationService = annotationService;
        this.conceptService = conceptService;
        this.hierarchyDecorator = new HierarchyDecorator(conceptService);
    }

    @Override
    public CompletableFuture<ResolvedConceptSelection> resolveConceptSelection(ConceptSelection conceptSelection) {
        return hierarchyDecorator.findConceptNames(conceptSelection.getConceptName(),
                conceptSelection.isExtendToParent(),
                conceptSelection.isExtendToSiblings(),
                conceptSelection.isExtendToChildren(),
                conceptSelection.isExtendToDescendants())
                .thenApplyAsync(list -> new ResolvedConceptSelection(conceptSelection, list));
    }

    @Override
    public CompletableFuture<List<String>> findDescendantNamesAsStrings(String conceptName) {
        return hierarchyDecorator.findConceptNames(conceptName, false, false, false, true);
    }

    @Override
    public CompletableFuture<List<Concept>> findAncestors(String conceptName) {
        return conceptService.findAncestors(conceptName)
            .thenApply(opt -> {
                List<Concept> children = new ArrayList<>();
                if (opt.isPresent()) {
                    Concept concept = opt.get();
                    accumulateChildren(concept, children);
                }
                return children;
            });
    }

    private void accumulateChildren(Concept concept, List<Concept> accum) {
        accum.add(concept);
        List<Concept> children = concept.getChildren();
        if (children != null) {
            children.forEach(c -> accumulateChildren(c, accum));
        }
    }

    @Override
    public CompletableFuture<Optional<Concept>> findConcept(String name) {
        final CompletableFuture<Optional<Concept>> cf = new CompletableFuture<>();
        conceptService.findAncestors(name)
                .thenAccept(opt -> {
                    if (opt.isPresent()) {
                        Concept concept = opt.get();
                        conceptService.findDetails(name)
                                .thenAccept(opt2 -> opt2.ifPresent(concept::setConceptDetails))
                                .thenAccept(v -> cf.complete(Optional.of(concept)));
                    }
                    else {
                        cf.complete(Optional.empty());
                    }
                });
        return cf;
    }

    @Override
    public CompletableFuture<List<String>> findConceptNamesAsStrings(String name, boolean extendToParent, boolean extendToSiblings, boolean extendToChildren, boolean extendToDescendants) {
        return hierarchyDecorator.findConceptNames(name, extendToParent, extendToSiblings, extendToChildren, extendToDescendants);
    }

    @Override
    public CompletableFuture<List<String>> findAllConceptNamesAsStrings() {
        return conceptService.findAllNames();
    }

    @Override
    public CompletableFuture<List<ILink>> findLinksByConceptNames(Collection<String> conceptNames) {
        return CompletableFuture.supplyAsync(() ->
                annotationService.findAssociations(conceptNames)
                .stream()
                .map(a -> (ILink) a)
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<ILink>> findAllLinks() {
        return conceptService.findAllTemplates()
                .thenApply(cats -> cats.stream()
                        .map(a -> (ILink) a)
                        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Optional<URL>> resolveImageURL(String conceptName) {
        return conceptService.findDetails(conceptName)
                .thenApply(opt -> opt.map(ConceptDetails::getMedia)
                        .flatMap(media -> media.stream()
                                .filter(ConceptMedia::isPrimary)
                                .map(ConceptMedia::getUrl)
                                .findFirst()));
    }

    @Override
    public CompletableFuture<Map<String, String>> getAnnotationViewMetadata() {
        return CompletableFuture.supplyAsync(annotationService::getMetadata);
    }

    @Override
    public CompletableFuture<Collection<?>> getAnnotationViewsUniqueValuesForColumn(String columnName) {
        return CompletableFuture.supplyAsync(()  -> annotationService.findDistinct(columnName));
    }

    @Override
    public CompletableFuture<List<Number>> getAnnotationViewsMinAndMaxForColumn(String columnName) {
        return CompletableFuture.supplyAsync(() -> annotationService.findMinMax(columnName));
    }

    @Override
    public CompletableFuture<List<Date>> getAnnotationViewsMinAndMaxDatesforColumn(String columnName) {
        return CompletableFuture.supplyAsync(() -> annotationService.findDateBounds(columnName));
    }

    @Override
    public Connection getAnnotationConnection() throws SQLException {
        return annotationService.getDatabaseConnection();
    }
}
