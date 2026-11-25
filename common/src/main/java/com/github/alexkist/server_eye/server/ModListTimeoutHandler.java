package com.github.alexkist.server_eye.server;

import com.github.alexkist.server_eye.Constants;
import com.github.alexkist.server_eye.config.ServerEyeConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ModListTimeoutHandler {

    private static final Map<UUID, Integer> PENDING = new ConcurrentHashMap<>();
    private static final Set<UUID> CONFIRMED = ConcurrentHashMap.newKeySet();

    public static void markReceived(ServerPlayer player) {
        CONFIRMED.add(player.getUUID());
        PENDING.remove(player.getUUID());
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        if (!CONFIRMED.remove(player.getUUID())) {
            PENDING.put(player.getUUID(), 0);
        }
    }

    public static void onPlayerLoggedOut(ServerPlayer player) {
        PENDING.remove(player.getUUID());
        CONFIRMED.remove(player.getUUID());
    }

    /** Call once per server tick (end of tick). */
    public static void tick(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }

        int timeoutSeconds = ServerEyeConfig.get().timeoutSeconds;
        if (timeoutSeconds <= 0) {
            PENDING.clear();
            return;
        }
        int timeoutTicks = timeoutSeconds * 20;

        Iterator<Map.Entry<UUID, Integer>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            int waited = entry.getValue() + 1;

            if (waited < timeoutTicks) {
                entry.setValue(waited);
                continue;
            }

            it.remove();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }
            Constants.LOG.info("{} was kicked for failing to send their mod list in time.",
                    player.getGameProfile().getName());
            player.connection.disconnect(Component.literal("""
                    Server Eye
                    Failed to receive mod list (Timed out)
                    This can happen due to a slow connection, or because Server Eye is not installed."""));
        }
    }

    private ModListTimeoutHandler() {}
}
