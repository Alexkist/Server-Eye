package com.github.alexkist.server_eye.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientModListHandler {

    public static void handle(ClientModListPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                process(payload, player);
            }
        });
    }

    private static void process(ClientModListPayload payload, ServerPlayer player) {

        ModListTimeoutHandler.markReceived(player);

        List<? extends String> blacklist = ServerEyeConfig.COMMON.blacklistedMods.get();
        List<? extends String> whitelist = ServerEyeConfig.COMMON.whitelistedPlayers.get();

        boolean logOnly = ServerEyeConfig.COMMON.logOnly.get();

        String username = player.getGameProfile().getName();

        boolean isWhitelisted = whitelist.stream()
                .anyMatch(entry -> entry.equalsIgnoreCase(username));

        if (isWhitelisted) {
            System.out.println("[Server-Eye] " + username + " is whitelisted.");
            return;
        }

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
            System.out.println("[Server-Eye] " + username
                + " has forbidden mod(s): " + String.join(", ", forbiddenMods));

            if (!logOnly) {
                player.connection.disconnect(
                    Component.literal("Server Eye\nYou are using disallowed modification(s):\n\n"
                        + String.join("\n", forbiddenMods))
                );
            }
        }
    }
}