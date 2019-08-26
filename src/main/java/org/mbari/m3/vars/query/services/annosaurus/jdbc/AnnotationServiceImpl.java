package org.mbari.m3.vars.query.services.annosaurus.jdbc;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.model.Association;
import org.mbari.m3.vars.query.services.AnnotationService;
import mbarix4j.sql.QueryFunction;
import mbarix4j.sql.QueryResults;
import mbarix4j.sql.QueryableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2017-11-02T12:21:00
 */
public class AnnotationServiceImpl implements AnnotationService {

    private final QueryableImpl queryable;
    private final String databaseIdentifier;
    private final Logger log = LoggerFactory.getLogger(getClass());


    public AnnotationServiceImpl(String url,
                                 String user,
                                 String password,
                                 String driver) {
        databaseIdentifier = url;
        queryable = new QueryableImpl(url, user, password, driver);
    }



    @Override
    public List<String> findAllNames() {
        QueryFunction<List<String>> queryFunction = resultSet -> {
            List<String> conceptNamesAsStrings = new ArrayList<>();
            while (resultSet.next()) {
                conceptNamesAsStrings.add(resultSet.getString(1));
            }

            return conceptNamesAsStrings;
        };

        String query = "SELECT concept FROM observations ORDER BY concept";

        return queryable.executeQueryFunction(query, queryFunction);
    }

    @Override
    public List<Association> findAssociations(Collection<String> conceptNames) {
        // Here's the function that extracts the contents of a results set
        final QueryFunction<Collection<Association>> queryFunction = resultSet -> {
            Collection<Association> associations = new ArrayList<>();
            while (resultSet.next()) {
                String linkName = resultSet.getString(1);
                String toConcept = resultSet.getString(2);
                String linkValue = resultSet.getString(3);
                associations.add(new Association(linkName, toConcept, linkValue));
            }
            return associations;
        };

        /*
         * Assemble a preparedStatement query to search for all annotations used for the respective
         * conceptnames.
         */
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT DISTINCT link_name, to_concept, link_value ");
        sb.append("FROM associations AS a JOIN observations AS o ON o.uuid = a.observation_uuid ");
        sb.append("WHERE ");
        for (Iterator i = conceptNames.iterator(); i.hasNext(); ) {
            String conceptName = (String) i.next();
            sb.append("concept = ?");
            if (i.hasNext()) {
                sb.append(" OR ");
            }
        }
        sb.append(" ORDER BY link_name, to_concept, link_value");

        // Execute Query
        List<Association> links = new ArrayList<>();
        try {
            Connection connection = queryable.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            int idx = 1;
            for (String name : conceptNames) {
                preparedStatement.setString(idx, name);
                idx++;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            links.addAll(queryFunction.apply(resultSet));
            preparedStatement.close();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to execute PreparedStatement of " + sb.toString(), e);
        }

        return links;
    }

    @Override
    public Integer countDistinct(String columnName) {
        QueryFunction<Integer> queryFunction = resultSet -> {
            Integer count = 0;
            while (resultSet.next()) {
                count++;
            }
            return count;
        };

        String query = "SELECT DISTINCT count(" + columnName + ") FROM annotations";

        return queryable.executeQueryFunction(query, queryFunction);
    }

    @Override
    public List findDistinct(String columnName) {
        String query = "SELECT DISTINCT " + columnName + " FROM annotations WHERE " + columnName +
                " IS NOT NULL ORDER BY " + columnName;

        final QueryFunction<List> queryFunction = resultSet -> {
            List values = new ArrayList<>();
            while (resultSet.next()) {
                values.add(resultSet.getObject(1));
            }

            return values;
        };

        return queryable.executeQueryFunction(query, queryFunction);
    }

    @Override
    public Map<String, String> getMetadata() {
        QueryFunction<Map<String, String>> queryFunction = resultSet -> {
            Map<String, String> map = new TreeMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colCount = metaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                map.put(metaData.getColumnLabel(i), metaData.getColumnClassName(i));
            }
            return map;
        };

        String query = "SELECT * FROM annotations WHERE observation_uuid = '" + UUID.randomUUID() + "'";

        return queryable.executeQueryFunction(query, queryFunction);
    }

    public List<Number> findMinMax(String columnName) {
        final String sql = "SELECT MIN(" + columnName + ") AS minValue, MAX(" + columnName +
                ") AS maxValue FROM Annotations WHERE " + columnName + " IS NOT NULL";

        List<Number> minMax = new ArrayList<>();
        try {
            QueryResults queryResults = queryable.executeQuery(sql);
            minMax.add((Number) queryResults.getResults("minValue").get(0));
            minMax.add((Number) queryResults.getResults("maxValue").get(0));
        }
        catch (Exception e) {
            log.error("An error occurred while executing the SQL statement: '" + sql + "'", e);
        }

        return minMax;
    }

    public List<Date> findDateBounds(String columnName) {
        final String sql = "SELECT MIN(" + columnName + ") AS minValue, MAX(" + columnName +
                ") AS maxValue FROM Annotations WHERE " + columnName + " IS NOT NULL";

        List<Date> minMax = new ArrayList<>();
        try {
            QueryResults queryResults = queryable.executeQuery(sql);
            minMax.add((Date) queryResults.getResults("minValue").get(0));
            minMax.add((Date) queryResults.getResults("maxValue").get(0));
        }
        catch (Exception e) {
            log.error("An error occurred while executing the SQL statement: '" + sql + "'", e);
        }

        return minMax;
    }


    @Override
    public String getDatabaseIdentifier() {
        return databaseIdentifier;
    }

    public Connection getDatabaseConnection() {
        try {
            return queryable.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database connection", e);
        }
    }

}
