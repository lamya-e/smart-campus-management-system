package com.campus.controller;

import com.campus.model.Module;
import com.campus.service.ModuleService;
import com.campus.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * Contrôleur pour la gestion des modules.
 */
public class ModuleController {

    @FXML private TableView<Module> modulesTable;
    @FXML private TableColumn<Module, String>  codeColumn;
    @FXML private TableColumn<Module, String>  nomColumn;
    @FXML private TableColumn<Module, Integer> coeffColumn;
    @FXML private TableColumn<Module, String>  semestreColumn;

    private final ObservableList<Module> data = FXCollections.observableArrayList();
    private final ModuleService service = new ModuleService();

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        coeffColumn.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        semestreColumn.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        modulesTable.setItems(data);
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(service.findAll());
        } catch (Exception e) {
            showError("Erreur lors du chargement des modules", e.getMessage());
        }
    }

    @FXML
    private void addModule() {
        showForm(null);
    }

    @FXML
    private void editModule() {
        Module selected = modulesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un module à modifier.");
            return;
        }
        showForm(selected);
    }

    @FXML
    private void deleteModule() {
        Module selected = modulesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un module à supprimer.");
            return;
        }
        if (confirm("Supprimer le module \"" + selected.getNom() + "\" ?")) {
            try {
                service.delete(selected.getId());
                refresh();
            } catch (Exception e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    private void showForm(Module module) {
        boolean isNew = (module == null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isNew ? "Ajouter un module" : "Modifier un module");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Champs
        TextField codeField     = new TextField();
        TextField nomField      = new TextField();
        Spinner<Integer> coeffSpinner = new Spinner<>(1, 20, 1);
        coeffSpinner.setEditable(true);
        ComboBox<String> semestreBox = new ComboBox<>();
        semestreBox.getItems().addAll("S1", "S2", "S3", "S4", "S5", "S6");
        semestreBox.setPromptText("Choisir...");

        // Placeholders
        codeField.setPromptText("Ex: INF101");
        nomField.setPromptText("Ex: Algorithmique");

        // Pré-remplir si modification
        if (!isNew) {
            codeField.setText(module.getCode());
            nomField.setText(module.getNom());
            coeffSpinner.getValueFactory().setValue(module.getCoefficient());
            semestreBox.setValue(module.getSemestre());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(400);
        grid.addRow(0, new Label("Code :"),        codeField);
        grid.addRow(1, new Label("Nom :"),         nomField);
        grid.addRow(2, new Label("Coefficient :"), coeffSpinner);
        grid.addRow(3, new Label("Semestre :"),    semestreBox);

        codeField.setPrefWidth(250);
        nomField.setPrefWidth(250);
        coeffSpinner.setPrefWidth(250);
        semestreBox.setPrefWidth(250);

        dialog.getDialogPane().setContent(grid);

        // Appliquer le CSS sur le dialog
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/campus/css/app.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Validation
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (codeField.getText().isBlank() || nomField.getText().isBlank()
                    || semestreBox.getValue() == null) {
                showWarning("Veuillez remplir tous les champs.");
                event.consume();
            }
        });

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (isNew) {
                        Module m = new Module();
                        m.setCode(codeField.getText().trim());
                        m.setNom(nomField.getText().trim());
                        m.setCoefficient(coeffSpinner.getValue());
                        m.setSemestre(semestreBox.getValue());
                        service.create(m);
                    } else {
                        module.setCode(codeField.getText().trim());
                        module.setNom(nomField.getText().trim());
                        module.setCoefficient(coeffSpinner.getValue());
                        module.setSemestre(semestreBox.getValue());
                        service.update(module);
                    }
                    refresh();
                } catch (Exception e) {
                    showError("Erreur lors de la sauvegarde", e.getMessage());
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        return alert.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ── Navigation ───────────────────────────────────────────────────────────
    @FXML private void navDashboard()   { SceneManager.showDashboard(); }
    @FXML private void navEtudiants()   { SceneManager.showEtudiants(); }
    @FXML private void navProfesseurs() { SceneManager.showProfesseurs(); }
    @FXML private void navModules()     { SceneManager.showModules(); }
    @FXML private void navNotes()       { SceneManager.showNotes(); }
}