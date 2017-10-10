package org.mbari.m3.vars.query.services.annotation;

import java.util.List;

import org.mbari.m3.vars.query.services.knowledgebase.ConceptNameValidator;
import org.mbari.m3.vars.query.services.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 1:59:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AssociationDAO extends DAO, ConceptNameValidator<Association> {

    List<Association> findAllByConceptNameAndValues(String conceptName,
            String linkName, String toConcept, String linkValue);

    Association findByPrimaryKey(Object primaryKey);

}
