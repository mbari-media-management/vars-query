package org.mbari.m3.vars.query.ui;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.mbari.m3.vars.query.ui.db.IConstraint;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Schlining
 * @since 2015-07-27T20:44:00
 */
public abstract class AbstractValuePanel extends HBox {

    private static final Pattern PATTERN = Pattern.compile("\\B[A-Z]+");
    private CheckBox constrainCheckBox;
    private CheckBox returnCheckBox;
    private final String valueName;
    private String title;

    public AbstractValuePanel(String valueName) {
        this.valueName = demungeRawValueName(valueName);
        constrainCheckBox = new CheckBox();
        constrainCheckBox.setTooltip(new Tooltip("constrain"));
        returnCheckBox = new CheckBox();
        returnCheckBox.setTooltip(new Tooltip("return"));
        getChildren().addAll(returnCheckBox, constrainCheckBox);
    }

    private String demungeRawValueName(String valueName) {
        // Get rid of underscores
        return valueName.replace('_', ' ');

        // TODO may need to check for CamelCase too.
    }

    public boolean isReturned() {
        return returnCheckBox.isSelected();
    }

    public void setReturned(boolean returned) {
       returnCheckBox.setSelected(returned);
    }

    public boolean isConstrained() {
        return constrainCheckBox.isSelected();
    }

    public String getTitle() {
        if (title == null) {
            title = valueName;
            if (!valueName.toUpperCase().equals(valueName)) {
                Matcher matcher = PATTERN.matcher(valueName);
                title = matcher.replaceAll(" $0");
            }
        }
        return title;
    }

    public String getValueName() {
        return valueName;
    }

    protected CheckBox getConstrainCheckBox() {
        return constrainCheckBox;
    }

    protected CheckBox getReturnCheckBox() {
        return returnCheckBox;
    }

    public abstract Optional<IConstraint> getConstraint();
}
