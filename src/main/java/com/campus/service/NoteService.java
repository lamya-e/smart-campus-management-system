package com.campus.service;

import com.campus.model.Note;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * Service CRUD pour les notes.
 * Utilise FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES pour mapper
 * les champs Java (camelCase) vers les colonnes Supabase (snake_case).
 */
public class NoteService {

    // GSON pour lecture (désérialisation)
    private static final Gson GSON_READ = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    // GSON pour écriture - sans serializeNulls → id null sera omis automatiquement
    private static final Gson GSON_WRITE = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final Type LIST_TYPE = new TypeToken<List<Note>>() {}.getType();
    private static final String TABLE = "notes";

    private final SupabaseClient client = SupabaseClient.getInstance();

    public List<Note> findAll() {
        String json = client.get(TABLE, "select=*");
        return GSON_READ.fromJson(json, LIST_TYPE);
    }

    public List<Note> findByEtudiant(UUID etudiantId) {
        String json = client.get(TABLE, "select=*&etudiant_id=eq." + etudiantId);
        return GSON_READ.fromJson(json, LIST_TYPE);
    }

    public Note findById(UUID id) {
        String json = client.get(TABLE, "select=*&id=eq." + id);
        List<Note> list = GSON_READ.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Note create(Note note) {
        note.setId(null); // null → omis automatiquement → Supabase génère l'UUID
        String body = GSON_WRITE.toJson(note);
        String json = client.post(TABLE, body);
        List<Note> list = GSON_READ.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Note update(Note note) {
        if (note.getId() == null) {
            throw new IllegalArgumentException("Note id is required for update");
        }
        String body = GSON_WRITE.toJson(note);
        String json = client.patch(TABLE, "id=eq." + note.getId(), body);
        List<Note> list = GSON_READ.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? note : list.get(0);
    }

    public void delete(UUID id) {
        client.delete(TABLE, "id=eq." + id);
    }
}
