package org.mbari.m3.vars.query.services.varskbserver.v1;


import org.mbari.m3.vars.query.model.Concept;
import org.mbari.m3.vars.query.model.ConceptAssociationTemplate;
import org.mbari.m3.vars.query.model.ConceptDetails;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * https://futurestud.io/tutorials/retrofit-add-custom-request-header
 *
 * This is the retrofit interface that defines all the web api calls for the vars-kb-service.
 * Man, does retrofit make this easy.
 *
 * @author Brian Schlining
 * @since 2017-05-11T16:40:00
 */
public interface KBWebService {

    @GET("phylogeny/down/{name}")
    Call<Concept> findDescendants(@Path("name") String name);

    @GET("phylogeny/up/{name}")
    Call<Concept> findAncestors(@Path("name") String name);

    @GET("concept")
    Call<List<String>> listConceptNames();

    @GET("concept/{name}")
    Call<ConceptDetails> findDetails(@Path("name") String name);

    @GET("links/")
    Call<List<ConceptAssociationTemplate>> findAllTemplates();

    @GET("links/{name}")
    Call<List<ConceptAssociationTemplate>> findTemplates(@Path("name") String name);

    @GET("links/{name}/using/{linkname}")
    Call<List<ConceptAssociationTemplate>> findTemplates(@Path("name") String name,
                                                         @Path("linkname") String linkname);

    @GET("concept/root")
    Call<ConceptDetails> findRootDetails();

}
