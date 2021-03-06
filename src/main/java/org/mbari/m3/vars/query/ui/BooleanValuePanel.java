package org.mbari.m3.vars.query.ui;

import org.mbari.m3.vars.query.ui.db.IConstraint;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-27T21:19:00
 */
public class BooleanValuePanel extends AbstractValuePanel {

    public BooleanValuePanel(String valueName) {
        super(valueName);
    }

    @Override
    public Optional<IConstraint> getConstraint() {
        return Optional.empty();
    }
}
