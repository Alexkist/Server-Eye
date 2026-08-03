package com.github.alexkist.server_eye.network;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "server_eye")
public class ModListTimeoutHandler {

    // Players who have joined but not yet sent their mod list
    private static final Map<ServerPlayer, Integer> pending = new ConcurrentHashMap<>();
    private static final Set<ServerPlayer> confirmed = ConcurrentHashMap.newKeySet();

    /** Called by ClientModListHandler once a player's payload has been processed. */
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
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || pending.isEmpty()) {
            return;
        }

        // Check if timeout was Disabled
        int timeoutSeconds = ServerEyeConfig.COMMON.timeoutSeconds.get();
        if (timeoutSeconds <= 0) {
            pending.clear();
            return; // Since it is set to 0, it would cause issues so we will return the Request :)
        }

        int timeoutTicks = timeoutSeconds * 20;

        pending.forEach((player, ticks) -> {
            int ticksWaited = ticks + 1;

            // Check if Player timed out
            if (ticksWaited >= timeoutTicks) {
                // Log
                System.out.println("[Server-Eye] " + player.getGameProfile().getName()
                    + " was kicked for failing to send there modlist in time.");

                // Disconnect
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