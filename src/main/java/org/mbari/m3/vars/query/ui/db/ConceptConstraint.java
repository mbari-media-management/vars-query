package org.mbari.m3.vars.query.ui.db;

import org.mbari.m3.vars.query.Constants;
import org.mbari.m3.vars.query.model.ILink;
import org.mbari.m3.vars.query.model.beans.ResolvedConceptSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-29T16:43:00
 */
public class ConceptConstraint implements IConstraint<ResolvedConceptSelection> {

    private final ResolvedConceptSelection resolvedConceptSelection;
    private final List<String> conceptNames;
    private final boolean hasLinkName;
    private final boolean hasToConcept;
    private final Boolean hasLinkValue;

    public ConceptConstraint(ResolvedConceptSelection resolvedConceptSelection) {
        this.resolvedConceptSelection = resolvedConceptSelection;

        conceptNames = new ArrayList<>();
        if (!isWildCard(resolvedConceptSelection.getConceptName())) {
            conceptNames.add(resolvedConceptSelection.getConceptName());
            conceptNames.addAll(resolvedConceptSelection.getConcepts());
        }

        ILink link = resolvedConceptSelection.getLink();
        if (link != null) {
            hasLinkName = !isWildCard(link.getLinkName());
            hasToConcept = !isWildCard(link.getToConcept());
            hasLinkValue = !isWildCard(link.getLinkValue());
        }
        else {
            hasLinkName = false;
            hasToConcept = false;
            hasLinkValue = false;
        }
    }

    private boolean isWildCard(String wc) {
        String u = wc.toUpperCase();
        return u.equals(Constants.WILD_CARD) || u.equals(ILink.VALUE_NIL);
    }

    @Override
    public int bind(PreparedStatement statement, int idx) throws SQLException {

        ILink link = resolvedConceptSelection.getLink();

        if (!conceptNames.isEmpty()) {
            for (String s : conceptNames) {
                statement.setString(idx, s);
                idx++;
            }
            if (hasToConcept) {
                statement.setString(idx, link.getToConcept());
            }
            else {
                for (String s : conceptNames) {
                    statement.setString(idx, s);
                    idx++;
                }
            }
        }
        else if (hasToConcept) {
            statement.setString(idx, link.getToConcept());
            idx++;
        }

        if (hasLinkName) {
            statement.setString(idx, link.getLinkName());
            idx++;
        }

        if (hasLinkValue) {
            statement.setString(idx, link.getLinkValue());
            idx++;
        }

        return idx;
    }

    @Override
    public String toSQLClause() {
        if (conceptNames.isEmpty()
                && !hasLinkName
                && !hasToConcept
                && !hasLinkValue) {
            return "";
        }

        ILink link = resolvedConceptSelection.getLink();

        StringBuilder sb = new StringBuilder("(");

        if (!conceptNames.isEmpty()) {
            String cns = conceptNames.stream()
                    .map(s -> "'" + s + "'")
                    .collect(Collectors.joining(", "));

            sb.append("(concept IN (");
            sb.append(cns);
            sb.append(")");


            if (hasToConcept) {
                sb.append(" AND to_concept = '").append(link.getToConcept()).append("'");
            }
            else {
                sb.append(" OR to_concept IN (")
                        .append(cns)
                        .append(")");
            }

            sb.append(")");
        }
        else if (hasToConcept) {
            sb.append("to_concept = '").append(link.getToConcept()).append("'");
        }

        if (hasLinkName) {
            sb.append(" AND link_name = '").append(link.getLinkName()).append("'");
        }

        if (hasLinkValue) {
            sb.append(" AND link_value = '").append(link.getLinkValue()).append("'");
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    public String toPreparedStatementTemplate() {
        if (conceptNames.isEmpty()
                && !hasLinkName
                && !hasToConcept
                && !hasLinkValue) {
            return "";
        }

        StringBuilder sb = new StringBuilder("(");

        if (!conceptNames.isEmpty()) {
            String cns = conceptNames.stream()
                    .map(s -> "?")
                    .collect(Collectors.joining(", "));

            sb.append("(concept IN (");
            sb.append(cns);
            sb.append(")");


            if (hasToConcept) {
                sb.append(" AND to_concept = ? ");
            }
            else {
                sb.append(" OR to_concept IN (")
                        .append(cns)
                        .append(")");
            }

            sb.append(")");
        }
        else if (hasToConcept) {
            sb.append("to_concept = ?");
        }


        if (hasLinkName) {
            if (sb.length() > 1) {
                sb.append(" AND");
            }
            sb.append(" link_name = ?");
        }

        if (hasLinkValue) {
            if (sb.length() > 1) {
                sb.append(" AND");
            }
            sb.append(" link_value = ?");
        }
        sb.append(")");

        return sb.toString();
    }
}
