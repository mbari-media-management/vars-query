package org.mbari.m3.vars.query.old;

/*
 * @(#)Toolbelt.java   2009.11.15 at 07:47:13 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import com.google.inject.Inject;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationDAOFactory;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationFactory;
import org.mbari.m3.vars.query.old.services.annotation.AnnotationPersistenceService;
import org.mbari.m3.vars.query.old.services.knowledgebase.KnowledgebaseDAOFactory;
import org.mbari.m3.vars.query.old.services.knowledgebase.KnowledgebaseFactory;
import org.mbari.m3.vars.query.old.services.query.QueryPersistenceService;
import org.mbari.m3.vars.query.old.services.misc.MiscDAOFactory;
import org.mbari.m3.vars.query.old.services.misc.MiscFactory;
import org.mbari.m3.vars.query.old.util.PersistenceCache;
import org.mbari.m3.vars.query.old.util.PersistenceCacheProvider;

/**
 *
 */
public class ToolBelt {

    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private final MiscDAOFactory miscDAOFactory;
    private final MiscFactory miscFactory;
    private final PersistenceCache persistenceCache;
    private final AnnotationPersistenceService annotationPersistenceService;
    private final QueryPersistenceService queryPersistenceService;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param annotationFactory
     * @param annotationPersistenceService
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     * @param miscDAOFactory
     * @param miscFactory
     * @param persistenceCacheProvider
     * @param queryPersistenceService
     */
    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory,
                    AnnotationFactory annotationFactory,
                    KnowledgebaseDAOFactory knowledgebaseDAOFactory,
                    KnowledgebaseFactory knowledgebaseFactory,
                    MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
                    PersistenceCacheProvider persistenceCacheProvider,
                    AnnotationPersistenceService annotationPersistenceService,
                    QueryPersistenceService queryPersistenceService) {
        this.annotationDAOFactory = annotationDAOFactory;
        this.annotationPersistenceService = annotationPersistenceService;
        this.annotationFactory = annotationFactory;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.knowledgebaseFactory = knowledgebaseFactory;
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        this.persistenceCache = new PersistenceCache(persistenceCacheProvider);
        this.queryPersistenceService = queryPersistenceService;
    }

    /**
     * @return
     */
    public AnnotationDAOFactory getAnnotationDAOFactory() {
        return annotationDAOFactory;
    }

    /**
     * @return
     */
    public AnnotationFactory getAnnotationFactory() {
        return annotationFactory;
    }


    /**
     * @return
     */
    public KnowledgebaseDAOFactory getKnowledgebaseDAOFactory() {
        return knowledgebaseDAOFactory;
    }

    /**
     * @return
     */
    public KnowledgebaseFactory getKnowledgebaseFactory() {
        return knowledgebaseFactory;
    }

    /**
     * @return
     */
    public MiscDAOFactory getMiscDAOFactory() {
        return miscDAOFactory;
    }

    /**
     * @return
     */
    public MiscFactory getMiscFactory() {
        return miscFactory;
    }

    /**
     * @return
     */
    public PersistenceCache getPersistenceCache() {
        return persistenceCache;
    }

    /**
     * @return
     */
    public AnnotationPersistenceService getAnnotationPersistenceService() {
        return annotationPersistenceService;
    }


    /**
     * @return
     */
    public QueryPersistenceService getQueryPersistenceService() {
        return queryPersistenceService;
    }


}

