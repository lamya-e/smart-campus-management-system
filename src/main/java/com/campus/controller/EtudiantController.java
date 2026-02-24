package com.campus.controller;

import com.campus.model.Etudiant;
import com.campus.service.EtudiantService;
import com.campus.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

/**
 * Contrôleur pour la gestion des étudiants.
 */
public class EtudiantController {

    @FXML private TableView<Etudiant> etudiantsTable;
    @FXML private TableColumn<Etudiant, String> cneColumn;
    @FXML private TableColumn<Etudiant, String> nomColumn;
    @FXML private TableColumn<Etudiant, String> prenomColumn;
    @FXML private TableColumn<Etudiant, String> filiereColumn;
    @FXML private TableColumn<Etudiant, String> niveauColumn;

    private final ObservableList<Etudiant> data = FXCollections.observableArrayList();
    private final EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        cneColumn.setCellValueFactory(new PropertyValueFactory<>("cne"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        filiereColumn.setCellValueFactory(new PropertyValueFactory<>("filiere"));
        niveauColumn.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        etudiantsTable.setItems(data);
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(service.findAll());
        } catch (Exception e) {
            showError("Erreur lors du chargement des étudiants", e.getMessage());
        }
    }

    @FXML
    private void addEtudiant() {
        showForm(null);
    }

    @FXML
    private void editEtudiant() {
        Etudiant selected = etudiantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un étudiant à modifier.");
            return;
        }
        showForm(selected);
    }

    @FXML
    private void deleteEtudiant() {
        Etudiant selected = etudiantsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un étudiant à supprimer.");
            return;
        }
        if (confirm("Supprimer l'étudiant " + selected.getPrenom() + " " + selected.getNom() + " ?")) {
            try {
                service.delete(selected.getId());
                refresh();
            } catch (Exception e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    private void showForm(Etudiant etudiant) {
        boolean isNew = (etudiant == null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isNew ? "Ajouter un étudiant" : "Modifier un étudiant");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Champs
        TextField cneField     = new TextField();
        TextField nomField     = new TextField();
        TextField prenomField  = new TextField();
        TextField filiereField = new TextField();
        ComboBox<String> niveauBox = new ComboBox<>();
        niveauBox.getItems().addAll("S1", "S2", "S3", "S4", "S5", "S6");
        niveauBox.setPromptText("Choisir...");

        // Placeholders
        cneField.setPromptText("Ex: P123456789");
        nomField.setPromptText("Nom de famille");
        prenomField.setPromptText("Prénom");
        filiereField.setPromptText("Ex: Génie Informatique");

        // Pré-remplir si modification
        if (!isNew) {
            cneField.setText(etudiant.getCne());
            nomField.setText(etudiant.getNom());
            prenomField.setText(etudiant.getPrenom());
            filiereField.setText(etudiant.getFiliere());
            niveauBox.setValue(etudiant.getNiveau());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(400);
        grid.addRow(0, new Label("CNE :"),     cneField);
        grid.addRow(1, new Label("Nom :"),     nomField);
        grid.addRow(2, new Label("Prénom :"),  prenomField);
        grid.addRow(3, new Label("Filière :"), filiereField);
        grid.addRow(4, new Label("Niveau :"),  niveauBox);

        // Largeur colonnes
        cneField.setPrefWidth(250);
        nomField.setPrefWidth(250);
        prenomField.setPrefWidth(250);
        filiereField.setPrefWidth(250);
        niveauBox.setPrefWidth(250);

        dialog.getDialogPane().setContent(grid);

        // Appliquer le CSS sur le dialog
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/campus/css/app.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Validation au clic OK
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (cneField.getText().isBlank() || nomField.getText().isBlank()
                    || prenomField.getText().isBlank() || filiereField.getText().isBlank()
                    || niveauBox.getValue() == null) {
                showWarning("Veuillez remplir tous les champs.");
                event.consume(); // Empêche la fermeture du dialog
            }
        });

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (isNew) {
                        Etudiant e = new Etudiant();
                        e.setCne(cneField.getText().trim());
                        e.setNom(nomField.getText().trim());
                        e.setPrenom(prenomField.getText().trim());
                        e.setFiliere(filiereField.getText().trim());
                        e.setNiveau(niveauBox.getValue());
                        service.create(e);
                    } else {
                        etudiant.setCne(cneField.getText().trim());
                        etudiant.setNom(nomField.getText().trim());
                        etudiant.setPrenom(prenomField.getText().trim());
                        etudiant.setFiliere(filiereField.getText().trim());
                        etudiant.setNiveau(niveauBox.getValue());
                        service.update(etudiant);
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