package org.mbari.m3.vars.query.old.ui.sdkfx;

import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.footer.ActionFooter;
import com.guigarage.sdk.list.MediaList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mbari.m3.vars.query.old.model.beans.ConceptSelection;
import org.mbari.m3.vars.query.old.model.beans.ResolvedConceptSelection;
import org.mbari.m3.vars.query.old.ui.AppIcons;
import org.mbari.m3.vars.query.old.shared.rx.RXEventBus;
import org.mbari.m3.vars.query.old.messages.ShowConceptConstraintsWorkbenchMsg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:03:00
 */
public class BasicSearchWorkbench extends WorkbenchView {

    private final MediaList<ConceptMedia> mediaList = new MediaList<>();
    private final ObservableList<ConceptSelection> conceptSelections = FXCollections.observableArrayList();
    private final RXEventBus eventBus;

    public BasicSearchWorkbench(RXEventBus eventBus) {
        this.eventBus = eventBus;
        ActionFooter footer = new ActionFooter();
        footer.addAction(new Action(AppIcons.PLUS, "Add Search Term",
                () -> eventBus.send(new ShowConceptConstraintsWorkbenchMsg())));
        footer.addAction(new Action(AppIcons.TRASH, "Remove All",
                () -> mediaList.getItems().clear()));
        setFooterNode(footer);
        setCenterNode(mediaList);
    }

    public ObservableList<ConceptMedia> getConceptMedia() {
        return mediaList.getItems();
    }

    public List<ResolvedConceptSelection> getConceptSelections() {
        return getConceptMedia().stream()
                .map(ConceptMedia::getConceptSelection)
                .collect(Collectors.toList());
    }

}
