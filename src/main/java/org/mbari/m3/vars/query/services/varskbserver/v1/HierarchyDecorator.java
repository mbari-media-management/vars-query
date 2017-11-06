package org.mbari.m3.vars.query.services.varskbserver.v1;

import com.google.common.collect.Lists;
import org.mbari.m3.vars.query.model.Concept;
import org.mbari.m3.vars.query.services.ConceptService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * @author Brian Schlining
 * @since 2017-11-02T09:28:00
 */
public class HierarchyDecorator {
    private final ConceptService conceptService;

    public HierarchyDecorator(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    /**
     * Find concepts based relationship to the provided one
     * @param name The conceptName of interest
     * @param extendToParent true = include parent
     * @param extendToSiblings true = include siblings (the parents children)
     * @param extendToChildren true = include children
     * @param extendToDescendants truen = include all dscendants (including children)
     * @return A collection of concept names that match the criteria.
     */
    public CompletableFuture<List<String>> findConceptNames(String name,
                                                            boolean extendToParent,
                                                            boolean extendToSiblings,
                                                            boolean extendToChildren,
                                                            boolean extendToDescendants) {

        // This is the future that we return. It's a loooong ascync journey to fill this
        CompletableFuture<List<String>> conceptNamesFuture = new CompletableFuture<>();

        // Final storage of all concept names
        List<String> conceptNames = new CopyOnWriteArrayList<>();

        // This future is completed when all the concepts (parent, siblings, children and/or
        // descendants have been fetched. When completed it loads the concept details
        // for each concept so we can get the alternate names too.
        // When all the alternate names (i.e. ConceptDetails are loaded it completes the
        // conceptNamesFuture.
        CompletableFuture<List<Concept>> concepts = new CompletableFuture<>();
        concepts.thenAccept(cs -> {
            CompletableFuture[] cfs = cs.stream()
                    .map(c -> conceptService.findDetails(c.getName())
                            .thenAccept(opt -> opt.ifPresent(c::setConceptDetails))
                            .thenAccept(v -> {
                                conceptNames.add(c.getName());
                                if (c.getConceptDetails() != null) {
                                    conceptNames.addAll(c.getConceptDetails().getAlternateNames());
                                }
                            }))
                    .toArray(i -> new CompletableFuture[i]);
            CompletableFuture.allOf(cfs)
                    .thenRun(() -> conceptNamesFuture.complete(conceptNames));
        });

        // Load descendants
        CompletableFuture<List<Concept>> f0 = (extendToChildren || extendToDescendants) ?
            loadDescendants(name, extendToChildren) :
            CompletableFuture.completedFuture(new ArrayList<>());

        // Load ancestors
        CompletableFuture<List<Concept>> f1 =  (extendToParent || extendToSiblings) ?
            loadAncestors(name, extendToSiblings) :
            CompletableFuture.completedFuture(new ArrayList<>());

        // When the desc/ancestor futures complete, complete the 'concepts' future
        CompletableFuture.allOf(f0, f1).thenAccept(v -> {
            Set<Concept> conceptSet = new HashSet<>();
            try {
                conceptSet.addAll(f0.get());
                conceptSet.addAll(f1.get());
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to load concept hierarchy", e);
            }
            List<Concept> conceptList = new ArrayList<>(conceptSet);
            concepts.complete(conceptList);
        });

        // We also sort the names
        return conceptNamesFuture.thenApply(names -> {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            return names;
        });


    }

    private CompletableFuture<List<Concept>> loadDescendants(String name, boolean extendToChildren) {
        return conceptService.findDescendants(name)
                .thenApply(opt -> {
                    if (opt.isPresent()) {
                        List<Concept> accum = new ArrayList<>();
                        accumulateDescendants(opt.get(), accum, extendToChildren);
                        return accum;
                    }
                    else {
                        return new ArrayList<Concept>();
                    }
                });
    }

    private void accumulateDescendants(Concept concept, List<Concept> accum, boolean extendToChildren) {
        accum.add(concept);
        List<Concept> children = concept.getChildren();
        if (extendToChildren) { // Only include children
            accum.addAll(children);
        }
        else if (children != null){                  // Otherwise include all descendants
            children.forEach(child -> accumulateDescendants(child, accum, false));
        }
    }

    private CompletableFuture<List<Concept>> loadAncestors(String name, boolean extendToSiblings) {
        CompletableFuture<List<Concept>> f = new CompletableFuture<>();

        conceptService.findAncestors(name)
                .thenAccept(opt -> {
                    if (opt.isPresent()) {
                        Concept concept = opt.get();
                        Optional<Concept> parentOpt0 = Concept.parent(concept, name);
                        if (parentOpt0.isPresent() && extendToSiblings) {
                            conceptService.findDescendants(parentOpt0.get().getName())
                                .thenAccept(parentOpt1 -> {
                                    ArrayList<Concept> concepts = Lists.newArrayList(parentOpt0.get(), concept);
                                    parentOpt1.ifPresent(c -> concepts.addAll(c.getChildren()));
                                    f.complete(concepts);
                                });
                        }
                        else if (parentOpt0.isPresent()){
                            f.complete(Lists.newArrayList(parentOpt0.get(), concept));
                        }
                        else {
                            f.complete(Lists.newArrayList(concept));
                        }
                    }
                    else {
                        f.complete(new ArrayList<>());
                    }
                });

        return f;
    }

}
