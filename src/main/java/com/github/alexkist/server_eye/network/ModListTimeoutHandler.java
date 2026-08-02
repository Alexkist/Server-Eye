package com.github.alexkist.server_eye.network;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod.EventBusSubscriber(modid = "server_eye")
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
    public static void onServerTick(ServerTickEvent.Post event) {
        if (pending.isEmpty()) {
            return;
        }

        int timeoutSeconds = ServerEyeConfig.COMMON.timeoutSeconds.get();
        if (timeoutSeconds <= 0) {
            return;
        }

        int timeoutTicks = timeoutSeconds * 20;

        pending.forEach((player, ticks) -> {
            int ticksWaited = ticks + 1;

            if (ticksWaited >= timeoutTicks) {
                player.connection.disconnect(
                    Component.literal("Server Eye\nFailed to receive mod list (Timed out)\n"
                        + "This can happen due to a slow connection, or if a required mod is missing.")
                );
                pending.remove(player);
            } else {
                pending.put(player, ticksWaited);
            }
        });
    }
}