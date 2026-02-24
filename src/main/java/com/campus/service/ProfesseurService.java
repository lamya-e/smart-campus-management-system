package com.campus.service;

import com.campus.model.Professeur;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * Service CRUD pour les professeurs.
 */
public class ProfesseurService {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type LIST_TYPE = new TypeToken<List<Professeur>>() {}.getType();
    private static final String TABLE = "professeurs";

    private final SupabaseClient client = SupabaseClient.getInstance();

    public List<Professeur> findAll() {
        String json = client.get(TABLE, "select=*");
        return GSON.fromJson(json, LIST_TYPE);
    }

    public Professeur findById(UUID id) {
        String json = client.get(TABLE, "select=*&id=eq." + id);
        List<Professeur> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Professeur create(Professeur professeur) {
        professeur.setId(null);
        String body = GSON.toJson(professeur);
        String json = client.post(TABLE, body);
        List<Professeur> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Professeur update(Professeur professeur) {
        if (professeur.getId() == null) {
            throw new IllegalArgumentException("Professeur id is required for update");
        }
        String body = GSON.toJson(professeur);
        String json = client.patch(TABLE, "id=eq." + professeur.getId(), body);
        List<Professeur> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? professeur : list.get(0);
    }

    public void delete(UUID id) {
        client.delete(TABLE, "id=eq." + id);
    }
}

