package com.campus.model;

import java.util.UUID;

/**
 * Représente un professeur dans le système.
 * Correspond à la table \"professeurs\" dans Supabase.
 */
public class Professeur {

    private UUID id;
    private String code;
    private String nom;
    private String prenom;
    private String specialite;
    private String email;

    public Professeur() {
    }

    public Professeur(UUID id, String code, String nom, String prenom,
                      String specialite, String email) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return nom + \" \" + prenom + \" (\" + code + \")\";
    }
}

