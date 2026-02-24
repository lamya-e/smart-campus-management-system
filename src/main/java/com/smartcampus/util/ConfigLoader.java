package com.smartcampus.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads database configuration from config.properties or .env.
 * Precedence: .env (if present) overrides config.properties.
 * Do NOT commit .env or config.properties with real credentials.
 */
public final class ConfigLoader {

    public static final String KEY_URL = "database.url";
    public static final String KEY_USERNAME = "database.username";
    public static final String KEY_PASSWORD = "database.password";

    private static final String ENV_FILE = ".env";
    private static final String CONFIG_FILE = "config.properties";

    private ConfigLoader() {
    }

    /**
     * Returns properties with database.url, database.username, database.password.
     * Throws RuntimeException if URL is missing after loading.
     */
    public static Properties load() {
        Properties props = new Properties();

        // 1. Load config.properties from classpath then from current directory
        loadFromClasspath(props, CONFIG_FILE);
        loadFromFile(props, Paths.get(System.getProperty("user.dir"), CONFIG_FILE));

        // 2. Override with .env if present
        Path envPath = Paths.get(System.getProperty("user.dir"), ENV_FILE);
        if (Files.exists(envPath)) {
            loadEnvFile(props, envPath);
        }

        if (props.getProperty(KEY_URL) == null || props.getProperty(KEY_URL).isBlank()) {
            throw new RuntimeException(
                "Missing database.url. Set it in config.properties or .env (DATABASE_URL).");
        }
        return props;
    }

    private static void loadFromClasspath(Properties into, String name) {
        try (InputStream is = ConfigLoader.class.getResourceAsStream("/" + name)) {
            if (is != null) {
                into.load(is);
            }
        } catch (IOException ignored) {
        }
    }

    private static void loadFromFile(Properties into, Path path) {
        if (!Files.exists(path)) return;
        try (InputStream is = Files.newInputStream(path)) {
            into.load(is);
        } catch (IOException ignored) {
        }
    }

    /**
     * Simple .env parser: KEY=VALUE per line, skip comments and blanks.
     * Maps DATABASE_URL -> database.url, DATABASE_USERNAME -> database.username, DATABASE_PASSWORD -> database.password.
     */
    private static void loadEnvFile(Properties into, Path path) {
        try {
            for (String line : Files.readAllLines(path)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                } else if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1);
                }
                if ("DATABASE_URL".equalsIgnoreCase(key)) {
                    into.setProperty(KEY_URL, value);
                } else if ("DATABASE_USERNAME".equalsIgnoreCase(key) || "DB_USERNAME".equalsIgnoreCase(key)) {
                    into.setProperty(KEY_USERNAME, value);
                } else if ("DATABASE_PASSWORD".equalsIgnoreCase(key) || "DB_PASSWORD".equalsIgnoreCase(key)) {
                    into.setProperty(KEY_PASSWORD, value);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
