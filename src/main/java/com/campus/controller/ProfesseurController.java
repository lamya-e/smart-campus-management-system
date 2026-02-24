package com.campus.controller;

import com.campus.model.Professeur;
import com.campus.service.ProfesseurService;
import com.campus.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * Contrôleur pour la gestion des professeurs.
 */
public class ProfesseurController {

    @FXML private TableView<Professeur> professeursTable;
    @FXML private TableColumn<Professeur, String> codeColumn;
    @FXML private TableColumn<Professeur, String> nomColumn;
    @FXML private TableColumn<Professeur, String> prenomColumn;
    @FXML private TableColumn<Professeur, String> specialiteColumn;
    @FXML private TableColumn<Professeur, String> emailColumn;

    private final ObservableList<Professeur> data = FXCollections.observableArrayList();
    private final ProfesseurService service = new ProfesseurService();

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        professeursTable.setItems(data);
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(service.findAll());
        } catch (Exception e) {
            showError("Erreur lors du chargement des professeurs", e.getMessage());
        }
    }

    @FXML
    private void addProfesseur() {
        showForm(null);
    }

    @FXML
    private void editProfesseur() {
        Professeur selected = professeursTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un professeur à modifier.");
            return;
        }
        showForm(selected);
    }

    @FXML
    private void deleteProfesseur() {
        Professeur selected = professeursTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner un professeur à supprimer.");
            return;
        }
        if (confirm("Supprimer le professeur " + selected.getPrenom() + " " + selected.getNom() + " ?")) {
            try {
                service.delete(selected.getId());
                refresh();
            } catch (Exception e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    private void showForm(Professeur professeur) {
        boolean isNew = (professeur == null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isNew ? "Ajouter un professeur" : "Modifier un professeur");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Champs
        TextField codeField       = new TextField();
        TextField nomField        = new TextField();
        TextField prenomField     = new TextField();
        TextField specialiteField = new TextField();
        TextField emailField      = new TextField();

        // Placeholders
        codeField.setPromptText("Ex: PR001");
        nomField.setPromptText("Nom de famille");
        prenomField.setPromptText("Prénom");
        specialiteField.setPromptText("Ex: Informatique");
        emailField.setPromptText("Ex: prof@univesrite.ma");

        // Pré-remplir si modification
        if (!isNew) {
            codeField.setText(professeur.getCode());
            nomField.setText(professeur.getNom());
            prenomField.setText(professeur.getPrenom());
            specialiteField.setText(professeur.getSpecialite());
            emailField.setText(professeur.getEmail());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(420);
        grid.addRow(0, new Label("Code :"),        codeField);
        grid.addRow(1, new Label("Nom :"),         nomField);
        grid.addRow(2, new Label("Prénom :"),      prenomField);
        grid.addRow(3, new Label("Spécialité :"),  specialiteField);
        grid.addRow(4, new Label("Email :"),       emailField);

        codeField.setPrefWidth(260);
        nomField.setPrefWidth(260);
        prenomField.setPrefWidth(260);
        specialiteField.setPrefWidth(260);
        emailField.setPrefWidth(260);

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
                    || prenomField.getText().isBlank() || specialiteField.getText().isBlank()
                    || emailField.getText().isBlank()) {
                showWarning("Veuillez remplir tous les champs.");
                event.consume();
                return;
            }
            if (!emailField.getText().contains("@")) {
                showWarning("Veuillez entrer une adresse email valide.");
                event.consume();
            }
        });

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (isNew) {
                        Professeur p = new Professeur();
                        p.setCode(codeField.getText().trim());
                        p.setNom(nomField.getText().trim());
                        p.setPrenom(prenomField.getText().trim());
                        p.setSpecialite(specialiteField.getText().trim());
                        p.setEmail(emailField.getText().trim());
                        service.create(p);
                    } else {
                        professeur.setCode(codeField.getText().trim());
                        professeur.setNom(nomField.getText().trim());
                        professeur.setPrenom(prenomField.getText().trim());
                        professeur.setSpecialite(specialiteField.getText().trim());
                        professeur.setEmail(emailField.getText().trim());
                        service.update(professeur);
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