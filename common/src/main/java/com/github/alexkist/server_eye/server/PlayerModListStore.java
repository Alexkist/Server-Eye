package com.github.alexkist.server_eye.server;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerModListStore {

    private static final Map<UUID, List<String>> MOD_LISTS = new ConcurrentHashMap<>();

    public static void store(ServerPlayer player, List<String> mods) {
        MOD_LISTS.put(player.getUUID(), List.copyOf(mods));
    }

    public static List<String> get(ServerPlayer player) {
        return MOD_LISTS.get(player.getUUID());
    }

    /** Called by each loader's logout event. */
    public static void onPlayerLoggedOut(ServerPlayer player) {
        MOD_LISTS.remove(player.getUUID());
    }

    private PlayerModListStore() {}
}
