package com.github.alexkist.server_eye.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ClientModListHandler {

    public static void handle(ClientModListPayload payload, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                process(payload, player);
            }
        });
        ctx.setPacketHandled(true);
    }

    private static void process(ClientModListPayload payload, ServerPlayer player) {

        // --- Timeout check ---
        ModListTimeoutHandler.markReceived(player);

        List<? extends String> blacklist = ServerEyeConfig.COMMON.blacklistedMods.get();
        List<? extends String> whitelist = ServerEyeConfig.COMMON.whitelistedPlayers.get();

        boolean logOnly = ServerEyeConfig.COMMON.logOnly.get();

        // --- Whitelist check (username only) ---
        String username = player.getGameProfile().getName();

        boolean isWhitelisted = whitelist.stream()
                .anyMatch(entry -> entry.equalsIgnoreCase(username));

        if (isWhitelisted) {
            System.out.println("[Server-Eye] " + username + " is whitelisted.");
            return;
        }

        // --- Blacklist check ---
        Set<String> normalizedBlacklist = blacklist.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> forbiddenMods = new ArrayList<>();
        for (String mod : payload.mods()) {
            if (normalizedBlacklist.contains(mod.toLowerCase())) {
                forbiddenMods.add(mod);
            }
        }

        if (!forbiddenMods.isEmpty()) {
            // Log to Console
            System.out.println("[Server-Eye] " + username
                + " has forbidden mod(s): " + String.join(", ", forbiddenMods));

            // Check if the Server is in log only mode
            if (!logOnly) {
                player.connection.disconnect(
                    Component.literal("Server Eye\nYou are using disallowed modification(s):\n\n"
                        + String.join("\n", forbiddenMods))
                );
            }
        }
    }
}