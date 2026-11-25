package com.github.alexkist.server_eye.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.alexkist.server_eye.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ServerEyeConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path file;

    private static Values values = new Values();

    public static final class Values {
        public List<String> blacklistedMods = new ArrayList<>();

        public List<String> whitelistedPlayers = new ArrayList<>();

        public int timeoutSeconds = 10;

        public boolean logOnly = false;
    }

    public static Values get() {
        return values;
    }

    public static void load(Path configDir) {
        file = configDir.resolve(Constants.MOD_ID + ".json");
        reload();
    }

    public static void reload() {
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                Values read = GSON.fromJson(reader, Values.class);
                if (read != null) {
                    values = sanitize(read);
                }
            } catch (Exception e) {
                Constants.LOG.error("Could not read {}, using defaults", file, e);
                values = new Values();
            }
        }
        save(); // Writes the default settings on first start
    }

    private static Values sanitize(Values v) {
        if (v.blacklistedMods == null) v.blacklistedMods = new ArrayList<>();
        if (v.whitelistedPlayers == null) v.whitelistedPlayers = new ArrayList<>();
        v.timeoutSeconds = Math.max(0, Math.min(300, v.timeoutSeconds));
        return v;
    }

    private static void save() {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(values, writer);
            }
        } catch (IOException e) {
            Constants.LOG.error("Could not write {}", file, e);
        }
    }

    private ServerEyeConfig() {}
}
