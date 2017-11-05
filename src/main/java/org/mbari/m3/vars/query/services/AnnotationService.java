package org.mbari.m3.vars.query.services;

import org.mbari.m3.vars.query.model.Association;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Schlining
 * @since 2017-10-31T13:20:00
 */
public interface AnnotationService {

    /**
     * Find all names used in Annotations
     * @return All concept names used in annotations, sorted.
     */
    List<String> findAllNames();

    /**
     * Looks up all associations in a database that were used with Observations
     * containing the specified conceptNames
     *
     * @param conceptNames A collection of Strings representing the conceptnames to
     *  lookup
     * @return A collection of <code>AssociationBean</code>s representing the
     *  associations actually used to annotate Observations with the specifed
     *  conceptNames.
     */
    List<Association> findAssociations(Collection<String> conceptNames);

    /**
     * Returns the count of unique columns found in the table for a given column
     * @param columnName The database column
     * @return count of distinct values in that column
     */
    Integer countDistinct(String columnName);

    /**
     * Find all distinct values for a column
     * @param columnName
     * @return
     */
    List<?> findDistinct(String columnName);

    /**
     * Retrieves the metadata for the Annotation table. Returns are in
     * alphabetical order.
     *
     * @return A Map where key is the columns name as a String, value is the columns
     *  Object type as a String (e.g. "java.lang.String". (This would be the type returned by
     *  resultSet.getObject())
     */
    Map<String, String>  getMetadata();

    /**
     *
     * @return the db id. e.g. the JDBC URL
     */
    String getDatabaseIdentifier();

    /**
     * Find the minium and maxium values in a numeric column
     * @param columnName The name of the column
     * @return A 2-element list. The first element is the min and the
     *      second element is the max.
     */
    List<Number> findMinMax(String columnName);
}
