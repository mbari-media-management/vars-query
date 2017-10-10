package org.mbari.m3.vars.query.services.knowledgebase;

import org.mbari.sql.IQueryable;

/**
 * DAO used by the Knowledgebase Application for special operations
 */
public interface KnowledgebasePersistenceService extends IQueryable {

    boolean doesConceptNameExist(String conceptName);

    void updateConceptNameUsedByLinkTemplates(Concept concept);

}
