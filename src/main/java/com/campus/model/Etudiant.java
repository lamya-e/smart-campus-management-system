package com.campus.model;

import java.util.UUID;

/**
 * Représente un étudiant dans le système de campus.
 * Correspond à la table "etudiants" dans Supabase.
 */
public class Etudiant {

    private UUID id;
    private String cne;
    private String nom;
    private String prenom;
    private String filiere;
    private String niveau; // S1...S6

    public Etudiant() {
    }

    public Etudiant(UUID id, String cne, String nom, String prenom,
                    String filiere, String niveau) {
        this.id = id;
        this.cne = cne;
        this.nom = nom;
        this.prenom = prenom;
        this.filiere = filiere;
        this.niveau = niveau;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCne() {
        return cne;
    }

    public void setCne(String cne) {
        this.cne = cne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + cne + ")";
    }
}

