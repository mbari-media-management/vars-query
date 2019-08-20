/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.m3.vars.query.util;

import org.mbari.m3.vars.query.model.KnowledgebaseObject;
import org.mbari.m3.vars.query.model.AnnotationObject;

/**
 *
 * @author brian
 */
public interface PersistenceCacheProvider {

    void clear();

    void evict(AnnotationObject entity);

    void evict(KnowledgebaseObject entity);

}
