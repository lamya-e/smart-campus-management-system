package com.campus.service;

import com.campus.model.Etudiant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * Service de haut niveau pour les opérations CRUD sur les étudiants.
 * Utilise SupabaseClient pour effectuer les appels REST.
 */
public class EtudiantService {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type LIST_TYPE = new TypeToken<List<Etudiant>>() {}.getType();
    private static final String TABLE = "etudiants";

    private final SupabaseClient client = SupabaseClient.getInstance();

    public List<Etudiant> findAll() {
        String json = client.get(TABLE, "select=*");
        return GSON.fromJson(json, LIST_TYPE);
    }

    public Etudiant findById(UUID id) {
        String json = client.get(TABLE, "select=*&id=eq." + id);
        List<Etudiant> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Etudiant create(Etudiant etudiant) {
        // Let Supabase/Postgres generate UUID by default
        etudiant.setId(null);
        String body = GSON.toJson(etudiant);
        String json = client.post(TABLE, body);
        List<Etudiant> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Etudiant update(Etudiant etudiant) {
        if (etudiant.getId() == null) {
            throw new IllegalArgumentException("Etudiant id is required for update");
        }
        String body = GSON.toJson(etudiant);
        String json = client.patch(TABLE, "id=eq." + etudiant.getId(), body);
        List<Etudiant> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? etudiant : list.get(0);
    }

    public void delete(UUID id) {
        client.delete(TABLE, "id=eq." + id);
    }
}