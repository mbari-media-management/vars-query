package org.mbari.m3.vars.query.services;



import org.mbari.m3.vars.query.model.Concept;
import org.mbari.m3.vars.query.model.ConceptAssociationTemplate;
import org.mbari.m3.vars.query.model.ConceptDetails;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2017-05-11T15:41:00
 */
public interface ConceptService {

    /**
     * Fetch all concepts and return the root node. You can walk the tree to find other nodes.
     * @return
     */
    CompletableFuture<Concept> findRoot();

    /**
     * Retrieves details about a specific node, such as alternate names and media.
     * @param name The name of the node to search for, can be primary or other
     * @return The details for the Concept
     */
    CompletableFuture<Optional<ConceptDetails>> findDetails(String name);

    CompletableFuture<ConceptDetails> findRootDetails();

    /**
     *
     * @return A list of all concept names found in the knowledgebase.
     */
    CompletableFuture<List<String>> findAllNames();

    CompletableFuture<List<ConceptAssociationTemplate>> findAllTemplates();

    CompletableFuture<List<ConceptAssociationTemplate>> findTemplates(String name);

    CompletableFuture<List<ConceptAssociationTemplate>> findTemplates(String name, String linkname);
    /**
     * Fetch a branch of the concept tree from the provided name on down
     * @param name
     * @return
     */
    CompletableFuture<Optional<Concept>> findDescendants(String name);

    /**
     * Find a concept with all it's ancestors
     * @param name The name of concept of interest
     * @return
     */
    CompletableFuture<Optional<Concept>> findAncestors(String name);

    /**
     * Find concepts based relationship to the provided one
     * @param name The conceptName of interest
     * @param extendToParent true = include parent
     * @param extendToSiblings true = include siblings (the parents children)
     * @param extendToChildren true = include children
     * @param extendToDescendants truen = include all dscendants (including children)
     * @return A collection of concept names that match the criteria.
     */
    CompletableFuture<List<String>> findConceptNames(String name,
                                                             boolean extendToParent,
                                                             boolean extendToSiblings,
                                                             boolean extendToChildren,
                                                             boolean extendToDescendants);


}
