package org.mbari.m3.vars.query.model;


/**
 * @author Brian Schlining
 * @since 2017-05-11T16:55:00
 */
public class ConceptAssociationTemplate implements ILink {
    private String linkName;
    private String toConcept;
    private String linkValue;

    public static final ConceptAssociationTemplate NIL = new ConceptAssociationTemplate(Association.VALUE_NIL,
            Association.VALUE_NIL, Association.VALUE_NIL);

    public ConceptAssociationTemplate(String linkName, String toConcept, String linkValue) {
        this.linkName = linkName;
        this.toConcept = toConcept;
        this.linkValue = linkValue;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getToConcept() {
        return toConcept;
    }

    public String getLinkValue() {
        return linkValue;
    }

    @Override
    public String toString() {
        String s = linkName + " | "  + toConcept + " | " + linkValue;
        if (s.length() > 140) {
            s = s.substring(0, 140) + "...";
        }
        return s;
    }

    @Override
    public String getFromConcept() {
        return null;
    }

    @Override
    public void setLinkName(String linkName) {
        throw new UnsupportedOperationException("Not allowed");
    }

    @Override
    public void setToConcept(String toConcept) {
        throw new UnsupportedOperationException("Not allowed");
    }

    @Override
    public void setLinkValue(String linkValue) {
        throw new UnsupportedOperationException("Not allowed");
    }

    @Override
    public String stringValue() {
        return toString();
    }
}
