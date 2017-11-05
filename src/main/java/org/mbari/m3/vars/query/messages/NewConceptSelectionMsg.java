package org.mbari.m3.vars.query.messages;

import org.mbari.m3.vars.query.old.model.beans.ConceptSelection;
import org.mbari.m3.vars.query.old.shared.rx.messages.Msg;

/**
 * @author Brian Schlining
 * @since 2015-07-26T11:21:00
 */
public class NewConceptSelectionMsg implements Msg {

    private final ConceptSelection conceptSelection;

    public NewConceptSelectionMsg(ConceptSelection conceptSelection) {
        this.conceptSelection = conceptSelection;
    }

    public ConceptSelection getConceptSelection() {
        return conceptSelection;
    }
}
