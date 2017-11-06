package org.mbari.m3.vars.query.model;

/**
 * Interface used to indicate that the class has lazy-relations that will
 * need to be loaded before they can be referenced.
 */
public interface ILazy {

    void loadLazyRelations();
}
