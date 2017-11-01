package org.mbari.m3.vars.query.services.varskbserver.v1;



import org.mbari.m3.vars.query.model.Concept;
import org.mbari.m3.vars.query.model.ConceptAssociationTemplate;
import org.mbari.m3.vars.query.model.ConceptDetails;
import org.mbari.m3.vars.query.services.ConceptService;
import org.mbari.m3.vars.query.services.RetrofitWebService;
import org.mbari.m3.vars.query.utils.AsyncUtils;

import javax.inject.Inject;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service that calls the REST API for vampire-squid. This version does NO caching,
 * each call will be sent to the server.
 *
 * @author Brian Schlining
 * @since 2017-05-11T16:13:00
 */
public class KBConceptService implements ConceptService, RetrofitWebService {


    /** Underlying retrofit API service */
    private final KBWebService service;


    @Inject
    public KBConceptService(KBWebServiceFactory serviceFactory) {
        service = serviceFactory.create(KBWebService.class);
    }


    @Override
    public CompletableFuture<Concept> findRoot() {
        return sendRequest(service.findRootDetails())
                .thenCompose(root -> sendRequest(service.findDescendants(root.getName())));
    }

    @Override
    public CompletableFuture<Optional<ConceptDetails>> findDetails(String name) {
        return sendRequest(service.findDetails(name)).thenApply(Optional::ofNullable);
    }

    public CompletableFuture<ConceptDetails> findRootDetails() {
        return sendRequest(service.findRootDetails());
    }

    @Override
    public CompletableFuture<List<String>> findAllNames() {
        return sendRequest(service.listConceptNames());
    }

    @Override
    public CompletableFuture<List<ConceptAssociationTemplate>> findTemplates(String name) {
        return sendRequest(service.findTemplates(name));
    }

    @Override
    public CompletableFuture<List<ConceptAssociationTemplate>> findTemplates(String name,
                                                                             String linkname) {
        return sendRequest(service.findTemplates(name, linkname));
    }

    @Override
    public CompletableFuture<Optional<Concept>> findDescendants(String name) {
        return sendRequest(service.findDetails(name))
                .thenCompose(concept ->
                        sendRequest(service.findDescendants(name))
                                .thenApply(Optional::ofNullable));
    }

    public CompletableFuture<Optional<Concept>> findAncestors(String name) {
        return sendRequest(service.findDetails(name))
                .thenCompose(concept ->
                        sendRequest(service.findAncestors(name))
                                .thenApply(Optional::ofNullable));
    }

    @Override
    public CompletableFuture<List<ConceptAssociationTemplate>> findAllTemplates() {
        return sendRequest(service.findAllTemplates());
    }



    @Override
    public CompletableFuture<List<String>> findConceptNames(String name,
                                                                    boolean extendToParent,
                                                                    boolean extendToSiblings,
                                                                    boolean extendToChildren,
                                                                    boolean extendToDescendants) {

        CompletableFuture<List<Concept>> future = new CompletableFuture<>();


        List<Concept> concepts = new CopyOnWriteArrayList<>();

        CompletableFuture<Optional<Concept>> f0 = null;
        CompletableFuture<Optional<Concept>> f1 = null;

        if (extendToChildren || extendToDescendants) {
            f0 = findDescendants(name);
          }

        if (extendToParent || extendToSiblings) {
            f1 = findAncestors(name);
        }


        if (f0 != null) {
            Optional<Optional<Concept>> opt0 = AsyncUtils.await(f0, Duration.ofSeconds(10));
            opt0.ifPresent(opt -> opt.ifPresent(concept -> accumulateDescendants(concept, concepts, extendToChildren)));
        }

        if (f1 != null) {
            Optional<Optional<Concept>> opt1 = AsyncUtils.await(f1, Duration.ofSeconds(10));
            opt1.ifPresent(opt -> opt.ifPresent(concept -> accumulateAncestors(concept, concepts, extendToChildren)));
        }

    }

    /**
     *
     * @param concept
     * @param accum
     * @param extendToChildren
     */
    private void accumulateDescendants(Concept concept, List<Concept> accum, boolean extendToChildren) {
        accum.add(concept);
        if (extendToChildren) { // Only include children
            accum.addAll(concept.getChildren());
        }
        else {                  // Otherwise include all descendants
            concept.getChildren()
                    .forEach(child -> accumulateDescendants(child, accum, false));
        }
    }

    /**
     *
     * @param concept This will be the root concept, which has children down to target concept,
     *                each concept has one child to form the chain
     * @param accum
     * @param extendToSiblings
     */
    private void accumulateAncestors(Concept concept, List<Concept> accum, boolean extendToSiblings) {


        if (!accum.contains(concept)) {
            accum.add(concept);
        }
        if (!extendToSiblings) {
            accum.add()
        }
    }

    private Concept getLastConcept(Con)
}
