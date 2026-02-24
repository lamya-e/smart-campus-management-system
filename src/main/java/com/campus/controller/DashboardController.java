package com.campus.controller;

import com.campus.service.EtudiantService;
import com.campus.service.ModuleService;
import com.campus.service.ProfesseurService;
import com.campus.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur du tableau de bord.
 */
public class DashboardController {

    @FXML
    private Label totalEtudiantsLabel;

    @FXML
    private Label totalProfesseursLabel;

    @FXML
    private Label totalModulesLabel;

    private final EtudiantService etudiantService = new EtudiantService();
    private final ProfesseurService professeurService = new ProfesseurService();
    private final ModuleService moduleService = new ModuleService();

    @FXML
    public void initialize() {
        totalEtudiantsLabel.setText(String.valueOf(etudiantService.findAll().size()));
        totalProfesseursLabel.setText(String.valueOf(professeurService.findAll().size()));
        totalModulesLabel.setText(String.valueOf(moduleService.findAll().size()));
    }

    @FXML
    private void navDashboard() {
        SceneManager.showDashboard();
    }

    @FXML
    private void navEtudiants() {
        SceneManager.showEtudiants();
    }

    @FXML
    private void navProfesseurs() {
        SceneManager.showProfesseurs();
    }

    @FXML
    private void navModules() {
        SceneManager.showModules();
    }

    @FXML
    private void navNotes() {
        SceneManager.showNotes();
    }
}

