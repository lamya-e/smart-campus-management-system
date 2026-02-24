package com.campus.util;

import com.campus.model.Module;
import com.campus.model.Note;

import java.util.List;

/**
 * Encapsule la logique de calcul des notes selon le système marocain.
 */
public final class NoteCalculator {

    public enum Statut {
        VALIDE,
        RATTRAPAGE,
        NON_VALIDE
    }

    private NoteCalculator() {
    }

    /**
     * Calcule la note finale sur 20 (max entre normale et rattrapage si disponible).
     */
    public static double noteFinale(Note note) {
        if (note == null || note.getNoteNormale() == null) {
            return 0.0;
        }
        double normale = note.getNoteNormale();
        if (note.getNoteRattrapage() != null) {
            return Math.max(normale, note.getNoteRattrapage());
        }
        return normale;
    }

    /**
     * Détermine le statut (VALIDÉ, RATTRAPAGE, NON VALIDÉ) selon la note finale.
     */
    public static Statut statut(Note note) {
        double nf = noteFinale(note);
        if (nf >= 10.0) {
            return Statut.VALIDE;
        }
        if (nf >= 7.0) {
            return Statut.RATTRAPAGE;
        }
        return Statut.NON_VALIDE;
    }

    /**
     * Calcule la moyenne générale pondérée par les coefficients des modules.
     */
    public static double moyenneGenerale(List<Note> notes, List<Module> modules) {
        if (notes == null || notes.isEmpty() || modules == null || modules.isEmpty()) {
            return 0.0;
        }
        double somme = 0.0;
        int sommeCoeff = 0;
        for (Note note : notes) {
            Module module = modules.stream()
                .filter(m -> m.getId().equals(note.getModuleId()))
                .findFirst()
                .orElse(null);
            if (module == null || module.getCoefficient() <= 0) {
                continue;
            }
            double nf = noteFinale(note);
            somme += nf * module.getCoefficient();
            sommeCoeff += module.getCoefficient();
        }
        return sommeCoeff == 0 ? 0.0 : somme / sommeCoeff;
    }

    /**
     * Renvoie la mention correspondante à une moyenne générale.
     */
    public static String mention(double moyenne) {
        if (moyenne >= 16.0) return "Très Bien";
        if (moyenne >= 14.0) return "Bien";
        if (moyenne >= 12.0) return "Assez Bien";
        if (moyenne >= 10.0) return "Passable";
        return "Aucune";
    }
}

