package com.campus.controller;

import com.campus.model.Etudiant;
import com.campus.model.Module;
import com.campus.model.Note;
import com.campus.service.EtudiantService;
import com.campus.service.ModuleService;
import com.campus.service.NoteService;
import com.campus.util.NoteCalculator;
import com.campus.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des notes et le bulletin de notes d'un étudiant.
 */
public class NoteController {

    @FXML private ComboBox<Etudiant> etudiantCombo;
    @FXML private TableView<Note> notesTable;
    @FXML private TableColumn<Note, String> moduleColumn;
    @FXML private TableColumn<Note, Double> normaleColumn;
    @FXML private TableColumn<Note, Double> rattrapageColumn;
    @FXML private TableColumn<Note, String> statutColumn;
    @FXML private Label moyenneLabel;
    @FXML private Label mentionLabel;

    private final ObservableList<Note> data = FXCollections.observableArrayList();
    private final NoteService noteService       = new NoteService();
    private final EtudiantService etudiantService = new EtudiantService();
    private final ModuleService moduleService   = new ModuleService();

    private Map<UUID, Module> modulesById;
    private List<Module> allModules;

    @FXML
    public void initialize() {
        try {
            etudiantCombo.setItems(FXCollections.observableArrayList(etudiantService.findAll()));
            allModules = moduleService.findAll();
            modulesById = allModules.stream().collect(Collectors.toMap(Module::getId, m -> m));
        } catch (Exception e) {
            showError("Erreur de chargement", e.getMessage());
        }

        // Affichage nom + prénom dans le ComboBox étudiant
        etudiantCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Etudiant e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getPrenom() + " " + e.getNom());
            }
        });
        etudiantCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Etudiant e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getPrenom() + " " + e.getNom());
            }
        });

        // Affichage nom module dans le ComboBox module
        moduleColumn.setCellValueFactory(cell -> {
            Module m = modulesById.get(cell.getValue().getModuleId());
            return new javafx.beans.property.SimpleStringProperty(m != null ? m.getNom() : "?");
        });

        normaleColumn.setCellValueFactory(new PropertyValueFactory<>("noteNormale"));
        rattrapageColumn.setCellValueFactory(new PropertyValueFactory<>("noteRattrapage"));

        statutColumn.setCellValueFactory(cell -> {
            NoteCalculator.Statut st = NoteCalculator.statut(cell.getValue());
            return new javafx.beans.property.SimpleStringProperty(st.name());
        });

        // Colorer les statuts
        statutColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    switch (statut) {
                        case "VALIDE"      -> setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                        case "RATTRAPAGE"  -> setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                        case "NON_VALIDE"  -> setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                        default            -> setStyle("");
                    }
                }
            }
        });

        notesTable.setItems(data);
        etudiantCombo.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> loadNotes());
    }

    private void loadNotes() {
        Etudiant etu = etudiantCombo.getSelectionModel().getSelectedItem();
        if (etu == null) {
            data.clear();
            moyenneLabel.setText("—");
            mentionLabel.setText("—");
            return;
        }
        try {
            List<Note> notes = noteService.findByEtudiant(etu.getId());
            data.setAll(notes);
            double moyenne = NoteCalculator.moyenneGenerale(notes, allModules);
            moyenneLabel.setText(String.format("%.2f / 20", moyenne));
            mentionLabel.setText(NoteCalculator.mention(moyenne));
        } catch (Exception e) {
            showError("Erreur de chargement des notes", e.getMessage());
        }
    }

    @FXML
    private void addNote() {
        Etudiant etu = etudiantCombo.getSelectionModel().getSelectedItem();
        if (etu == null) {
            showWarning("Veuillez sélectionner un étudiant.");
            return;
        }
        showForm(new Note(), etu);
    }

    @FXML
    private void editNote() {
        Note selected = notesTable.getSelectionModel().getSelectedItem();
        Etudiant etu  = etudiantCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner une note à modifier.");
            return;
        }
        showForm(selected, etu);
    }

    @FXML
    private void deleteNote() {
        Note selected = notesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Veuillez sélectionner une note à supprimer.");
            return;
        }
        if (confirm("Supprimer la note sélectionnée ?")) {
            try {
                noteService.delete(selected.getId());
                loadNotes();
            } catch (Exception e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    private void showForm(Note note, Etudiant etu) {
        boolean isNew = (note.getId() == null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isNew ? "Ajouter une note" : "Modifier une note");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Champs
        ComboBox<Module> moduleBox = new ComboBox<>(FXCollections.observableArrayList(allModules));
        moduleBox.setPromptText("Choisir un module...");
        moduleBox.setPrefWidth(250);

        // Affichage nom module dans ComboBox
        moduleBox.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Module m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getCode() + " - " + m.getNom());
            }
        });
        moduleBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Module m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getCode() + " - " + m.getNom());
            }
        });

        TextField normaleField    = new TextField();
        TextField rattrapageField = new TextField();
        TextField anneeField      = new TextField("2024-2025");

        normaleField.setPromptText("Note /20 (ex: 14.5)");
        rattrapageField.setPromptText("Optionnel");
        normaleField.setPrefWidth(250);
        rattrapageField.setPrefWidth(250);
        anneeField.setPrefWidth(250);

        // Pré-remplir si modification
        if (!isNew) {
            Module m = modulesById.get(note.getModuleId());
            if (m != null) moduleBox.setValue(m);
            if (note.getNoteNormale()    != null) normaleField.setText(String.valueOf(note.getNoteNormale()));
            if (note.getNoteRattrapage() != null) rattrapageField.setText(String.valueOf(note.getNoteRattrapage()));
            if (note.getAnneeUniversitaire() != null) anneeField.setText(note.getAnneeUniversitaire());
        }

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Module :"),             moduleBox);
        grid.addRow(1, new Label("Note normale /20 :"),   normaleField);
        grid.addRow(2, new Label("Note rattrapage /20 :"), rattrapageField);
        grid.addRow(3, new Label("Année universitaire :"), anneeField);

        dialog.getDialogPane().setContent(grid);

        // Appliquer le CSS sur le dialog
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/campus/css/app.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Validation
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (moduleBox.getValue() == null || normaleField.getText().isBlank()) {
                showWarning("Module et note normale sont obligatoires.");
                event.consume();
                return;
            }
            try {
                double n = Double.parseDouble(normaleField.getText().trim());
                if (n < 0 || n > 20) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showWarning("La note normale doit être un nombre entre 0 et 20.");
                event.consume();
                return;
            }
            if (!rattrapageField.getText().isBlank()) {
                try {
                    double r = Double.parseDouble(rattrapageField.getText().trim());
                    if (r < 0 || r > 20) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    showWarning("La note de rattrapage doit être un nombre entre 0 et 20.");
                    event.consume();
                }
            }
        });

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    note.setEtudiantId(etu.getId());
                    note.setModuleId(moduleBox.getValue().getId());
                    note.setNoteNormale(Double.parseDouble(normaleField.getText().trim()));
                    String ratt = rattrapageField.getText().trim();
                    note.setNoteRattrapage(ratt.isBlank() ? null : Double.parseDouble(ratt));
                    note.setAnneeUniversitaire(anneeField.getText().trim());

                    if (isNew) noteService.create(note);
                    else       noteService.update(note);

                    loadNotes();
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