package org.mbari.m3.vars.query.services.knowledgebase;

/**
 * Tag interface for DAO objects that can validate concept names used by
 * the entity objects. This is to ensure that they are persisted using a
 * primary name.
 */
public interface ConceptNameValidator<T> {

    void validateName(T object, ConceptDAO conceptDAO);

}
