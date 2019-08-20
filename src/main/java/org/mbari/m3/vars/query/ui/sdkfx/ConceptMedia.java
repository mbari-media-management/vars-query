package org.mbari.m3.vars.query.ui.sdkfx;

import com.guigarage.sdk.util.Media;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.mbari.m3.vars.query.Constants;
import org.mbari.m3.vars.query.model.Concept;
import org.mbari.m3.vars.query.util.LinkUtilities;
import org.mbari.m3.vars.query.model.beans.ResolvedConceptSelection;

import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:06:00
 */
public class ConceptMedia implements Media {


    private final ResolvedConceptSelection conceptSelection;
    private Concept concept;

    private StringProperty title = new SimpleStringProperty();

    private StringProperty description = new SimpleStringProperty();

    private ObjectProperty<Image> image = new SimpleObjectProperty<>();

    public ConceptMedia(ResolvedConceptSelection conceptSelection) {
        if (conceptSelection == null) {
            throw new IllegalArgumentException("Concept arg can not be null");
        }
        this.conceptSelection = conceptSelection;
        init();
    }

    private void init() {

        String titleString = conceptSelection.getConceptName();
        if (!LinkUtilities.formatAsString(conceptSelection.getLink())
                .equals(LinkUtilities.formatAsString(Constants.WILD_CARD_LINK))) {
            titleString = titleString + " | " + LinkUtilities.formatAsString(conceptSelection.getLink());
        }

        title.set(titleString);

        String desc = conceptSelection.getConcepts().stream()
                .filter(s -> !s.equals(conceptSelection.getConceptName()))
                .collect(Collectors.joining(", "));

        description.set(desc);

        image.set(conceptSelection.getImage());


    }



    @Override
    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public ResolvedConceptSelection getConceptSelection() {
        return conceptSelection;
    }
}
