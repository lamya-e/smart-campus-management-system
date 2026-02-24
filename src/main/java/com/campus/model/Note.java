package com.campus.model;

import java.util.UUID;

/**
 * Représente une note d'un étudiant pour un module.
 * Correspond à la table \"notes\" dans Supabase.
 */
public class Note {

    private UUID id;
    private UUID etudiantId;
    private UUID moduleId;
    private Double noteNormale;      // /20
    private Double noteRattrapage;   // /20, nullable
    private String anneeUniversitaire;

    public Note() {
    }

    public Note(UUID id, UUID etudiantId, UUID moduleId,
                Double noteNormale, Double noteRattrapage,
                String anneeUniversitaire) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.moduleId = moduleId;
        this.noteNormale = noteNormale;
        this.noteRattrapage = noteRattrapage;
        this.anneeUniversitaire = anneeUniversitaire;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(UUID etudiantId) {
        this.etudiantId = etudiantId;
    }

    public UUID getModuleId() {
        return moduleId;
    }

    public void setModuleId(UUID moduleId) {
        this.moduleId = moduleId;
    }

    public Double getNoteNormale() {
        return noteNormale;
    }

    public void setNoteNormale(Double noteNormale) {
        this.noteNormale = noteNormale;
    }

    public Double getNoteRattrapage() {
        return noteRattrapage;
    }

    public void setNoteRattrapage(Double noteRattrapage) {
        this.noteRattrapage = noteRattrapage;
    }

    public String getAnneeUniversitaire() {
        return anneeUniversitaire;
    }

    public void setAnneeUniversitaire(String anneeUniversitaire) {
        this.anneeUniversitaire = anneeUniversitaire;
    }
}

