package org.mbari.m3.vars.query.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class ConceptSearchPaneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private HBox root;

    @FXML
    private CheckBox parentCheckBox;

    @FXML
    private CheckBox siblingsCheckBox;

    @FXML
    private CheckBox childrenCheckBox;

    @FXML
    private CheckBox descendantCheckBox;

    @FXML
    void initialize() {

    }
}
