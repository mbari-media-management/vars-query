/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.m3.vars.query.old.util;

import org.mbari.m3.vars.query.old.services.knowledgebase.KnowledgebaseObject;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationObject;

/**
 *
 * @author brian
 */
public interface PersistenceCacheProvider {

    void clear();

    void evict(AnnotationObject entity);

    void evict(KnowledgebaseObject entity);

}
