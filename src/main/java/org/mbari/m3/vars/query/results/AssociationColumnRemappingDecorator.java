package org.mbari.m3.vars.query.results;

import org.mbari.m3.vars.query.model.ILink;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-30T15:47:00
 */
public class AssociationColumnRemappingDecorator {

    /**
     * Execute this <b>before</b> using <i>coalesce</i>. This method will take
     * the associations and put each unique linkName into it's own column
     * @param queryResults
     */
//    public static QueryResults apply(QueryResults queryResults) {
//        Map<String, List<Object>> resultsMap = queryResults.copyData();
//        Map<String, List<Object>> associationsMap = new TreeMap<>();
//
//        // --- Find all distinct linkNames used in the Associations
//        if (resultsMap.containsKey("Associations")) {
//            List associationResults = resultsMap.get("Associations");
//            Set<String> linkNames = new HashSet<>();
//            for (Object a : associationResults) {
//                if (a != null) {
//                    String s = (String) a;
//                    String[] parts = s.split(ILink.DELIMITER);
//                    linkNames.add(parts[0].trim());
//                }
//            }
//
//            // --- Generate an empty list of the correct size to account for null values
//            for (String linkName : linkNames) {
//                List<Object> list = new ArrayList<>(queryResults.getRows());
//                for (int i = 0; i < queryResults.getRows(); i++) {
//                    list.add(null);
//                }
//                associationsMap.put(linkName, list);
//            }
//
//            // --- Put the association in the correct column
//            for (int i = 0; i < queryResults.getRows(); i++) {
//                String association = (String) associationResults.get(i);
//                if (association != null) {
//                    String[] parts = association.split(ILink.DELIMITER);
//                    String linkName = parts[0];
//                    List<Object> list = associationsMap.get(linkName);
//                    list.set(i, association);
//                }
//            }
//
//            resultsMap.remove("Associations");
//            resultsMap.putAll(associationsMap);
//        }
//
//        return new QueryResults(resultsMap);
//    }

    public static QueryResults apply(QueryResults queryResults) {

        List<String> associations = (List<String>) queryResults.getValues("Associations");

        // --- Find all distinct linkNames used in the Associations
        List<String> linkNames = associations.stream()
                .filter(Objects::nonNull)
                .map(s -> s.split(ILink.DELIMITER)[0].trim())
                .distinct()
                .collect(Collectors.toList());

        // --- Generate an empty array of the correct size to account for null values
        Map<String, String[]> associationsByColumn = new HashMap<>();
        linkNames.forEach(linkName -> associationsByColumn.put(linkName, new String[queryResults.getRows()]));

        // --- Sort the associations into the correct column
        for (int i = 0; i < associations.size(); i++) {
            String link = associations.get(i);
            if (link != null) {
                String linkName = link.split(ILink.DELIMITER)[0].trim();
                String[] a = associationsByColumn.get(linkName);
                a[i] = link;
            }
        }

        // --- Transform map to Map<String, List<String>>
        Map<String, List<Object>> associationsByColumns2 = new HashMap<>();
        for (String key : associationsByColumn.keySet()) {
            List<Object> value = Arrays.asList(associationsByColumn.get(key));
            associationsByColumns2.put(key, value);
        }

        Map<String, List<Object>> resultsMap = queryResults.copyData();

        resultsMap.remove("Associations");
        resultsMap.putAll(associationsByColumns2);

        return new QueryResults(resultsMap);

    }
}
