package com.github.alexkist.server_eye.network;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "server_eye")
public class ModListTimeoutHandler {

    private static final Map<ServerPlayer, Integer> pending = new ConcurrentHashMap<>();
    private static final Set<ServerPlayer> confirmed = ConcurrentHashMap.newKeySet();

    public static void markReceived(ServerPlayer player) {
        confirmed.add(player);
        pending.remove(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!confirmed.remove(player)) {
                pending.put(player, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            pending.remove(player);
            confirmed.remove(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (pending.isEmpty()) {
            return;
        }

        int timeoutSeconds = ServerEyeConfig.COMMON.timeoutSeconds.get();
        if (timeoutSeconds <= 0) {
            pending.clear();
            return;
        }

        int timeoutTicks = timeoutSeconds * 20;

        pending.forEach((player, ticks) -> {
            int ticksWaited = ticks + 1;

            if (ticksWaited >= timeoutTicks) {
                // Log
                System.out.println("[Server-Eye] " + player.getGameProfile().getName()
                    + " was kicked for failing to send their mod list in time.");

                // Disconnect
                player.connection.disconnect(
                    Component.literal("""
                        Server Eye
                        Failed to receive mod list (Timed out)
                        This can happen due to a slow connection, or because Server Eye is not installed.""")
                );
                pending.remove(player);
            } else {
                pending.put(player, ticksWaited);
            }
        });
    }
}