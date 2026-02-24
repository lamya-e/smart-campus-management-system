package com.campus.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

/**
 * Low-level HTTP client for calling Supabase REST API.
 * Configuration is read from /com/campus/config.properties on the classpath.
 */
public final class SupabaseClient {

    private static final SupabaseClient INSTANCE = new SupabaseClient();

    private final HttpClient httpClient;
    private final String restUrl;
    private final String apiKey;
    private final String authToken;

    private SupabaseClient() {
        Properties props = new Properties();
        try (InputStream is = SupabaseClient.class.getResourceAsStream("/com/campus/config.properties")) {
            if (is == null) {
                throw new IllegalStateException("Missing config file: /com/campus/config.properties");
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load Supabase config", e);
        }

        String baseUrl = Objects.requireNonNull(props.getProperty("supabase.url"), "supabase.url is required").trim();
        this.apiKey = Objects.requireNonNull(props.getProperty("supabase.key"), "supabase.key is required").trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.restUrl = baseUrl + "/rest/v1";
        this.authToken = this.apiKey;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static SupabaseClient getInstance() {
        return INSTANCE;
    }

    public String get(String path, String query) {
        String url = buildUrl(path, query);
        HttpRequest request = baseRequestBuilder(url)
                .GET()
                .build();
        return send(request);
    }

    public String post(String path, String jsonBody) {
        String url = buildUrl(path, null);
        HttpRequest request = baseRequestBuilder(url)
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return send(request);
    }

    public String patch(String path, String query, String jsonBody) {
        String url = buildUrl(path, query);
        HttpRequest request = baseRequestBuilder(url)
                .header("Prefer", "return=representation")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return send(request);
    }

    public String delete(String path, String query) {
        String url = buildUrl(path, query);
        HttpRequest request = baseRequestBuilder(url)
                .DELETE()
                .build();
        return send(request);
    }

    private HttpRequest.Builder baseRequestBuilder(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private String buildUrl(String path, String query) {
        StringBuilder sb = new StringBuilder();
        sb.append(restUrl);
        if (!path.startsWith("/")) {
            sb.append('/');
        }
        sb.append(path);
        if (query != null && !query.isBlank()) {
            if (!query.startsWith("?")) {
                sb.append('?');
            }
            sb.append(query);
        }
        return sb.toString();
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                return response.body();
            }
            throw new RuntimeException("Supabase request failed with status " + status + ": " + response.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Supabase request failed", e);
        }
    }
}