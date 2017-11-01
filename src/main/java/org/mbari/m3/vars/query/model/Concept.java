package org.mbari.m3.vars.query.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2017-05-11T15:51:00
 */
public class Concept {
    private String name;
    private String rank;
    private List<Concept> children;
    private ConceptDetails conceptDetails;

    public Concept(String name, String rank, List<Concept> children) {
        this.name = name;
        this.rank = rank;
        this.children = Collections.unmodifiableList(children.stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(Collectors.toList()));
    }

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public List<Concept> getChildren() {
        return children;
    }

    public ConceptDetails getConceptDetails() {
        return conceptDetails;
    }

    public void setConceptDetails(ConceptDetails conceptDetails) {
        this.conceptDetails = conceptDetails;
    }

    /**
     * @return An alphabetical listing of all descendants of this concept
     */
    public List<String> flatten() {
        return flatten(this);
    }


    private static List<String> flatten(Concept concept) {
        List<String> accum = new ArrayList<>();
        flatten(concept, accum);
        accum.sort(String::compareToIgnoreCase);
        return accum;
    }

    private static void flatten(Concept concept, List<String> accum) {
        accum.add(concept.getName());
        if (concept.getConceptDetails() != null) {
            accum.addAll(concept.getConceptDetails().getAlternateNames());
        }
        concept.getChildren()
                .forEach(c -> flatten(c, accum));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Concept concept = (Concept) o;

        return name.equals(concept.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * For phylogeny down, you might want the last descendant in the chain.
     * @param concept The concept of interest
     * @return The last concept in a single chain (i.e. from phylogeny down). If
     *  any concepts in that chain have more than one child then None is returned.
     */
    public static Optional<Concept> lastDescendant(Concept concept) {
        if (concept.getChildren() == null || concept.getChildren().isEmpty()) {
            return Optional.of(concept);
        }
        else if (concept.getChildren() != null && concept.getChildren().size() == 1) {
            return lastDescendant(concept.getChildren().get(0));
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Get the parent concept from a chain of concepts (ie. from phylogeny down)
     * @param concept The root of the hierarchy
     * @param conceptName The name of the concept whose parent we want
     * @return The parent concept
     */
    public static Optional<Concept> parent(Concept concept, String conceptName) {
        if (concept.getName().equals(conceptName)) {
            return Optional.of(concept);
        }
        else if (concept.getChildren() != null && concept.getChildren().size() == 1) {
            return parent(concept.getChildren().get(0), conceptName);
        }
        else {
            return Optional.empty();
        }
    }
}
