package com.campus.model;

import java.util.UUID;

/**
 * Représente un module d'enseignement.
 * Correspond à la table \"modules\" dans Supabase.
 */
public class Module {

    private UUID id;
    private String code;
    private String nom;
    private int coefficient;
    private String semestre;
    private UUID professeurId;

    public Module() {
    }

    public Module(UUID id, String code, String nom, int coefficient,
                  String semestre, UUID professeurId) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.coefficient = coefficient;
        this.semestre = semestre;
        this.professeurId = professeurId;
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

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public UUID getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(UUID professeurId) {
        this.professeurId = professeurId;
    }

    @Override
    public String toString() {
        return code + " - " + nom;
    }
}

