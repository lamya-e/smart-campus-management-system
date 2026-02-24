package com.campus.service;

import com.campus.model.Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * Service CRUD pour les modules.
 */
public class ModuleService {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type LIST_TYPE = new TypeToken<List<Module>>() {}.getType();
    private static final String TABLE = "modules";

    private final SupabaseClient client = SupabaseClient.getInstance();

    public List<Module> findAll() {
        String json = client.get(TABLE, "select=*");
        return GSON.fromJson(json, LIST_TYPE);
    }

    public Module findById(UUID id) {
        String json = client.get(TABLE, "select=*&id=eq." + id);
        List<Module> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Module create(Module module) {
        module.setId(null);
        String body = GSON.toJson(module);
        String json = client.post(TABLE, body);
        List<Module> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? null : list.get(0);
    }

    public Module update(Module module) {
        if (module.getId() == null) {
            throw new IllegalArgumentException("Module id is required for update");
        }
        String body = GSON.toJson(module);
        String json = client.patch(TABLE, "id=eq." + module.getId(), body);
        List<Module> list = GSON.fromJson(json, LIST_TYPE);
        return list.isEmpty() ? module : list.get(0);
    }

    public void delete(UUID id) {
        client.delete(TABLE, "id=eq." + id);
    }
}

