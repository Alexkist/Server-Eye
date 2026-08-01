package com.github.alexkist.server_eye.network;

import java.util.Map;
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

    /** Called by ClientModListHandler once a player's payload has been processed. */
    public static void markReceived(ServerPlayer player) {
        pending.remove(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            pending.put(player, 0);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            pending.remove(player);
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
            return; // Since it is set to 0, it would cause issues so we will return the Request :)
        }

        int timeoutTicks = timeoutSeconds * 20;

        // Add Entry
        pending.entrySet().removeIf(entry -> {
            ServerPlayer player = entry.getKey();
            int ticksWaited = entry.getValue() + 1;

            // Check if Player timed
            if (ticksWaited >= timeoutTicks) {
                player.connection.disconnect(
                    Component.literal("Server Eye\nFailed to receive mod list (Timed out)\n"
                        + "This can happen due to a slow connection, or if a required mod is missing.")
                );
                return true;
            }

            entry.setValue(ticksWaited);
            return false;
        });
    }
}