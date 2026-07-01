package com.github.alexkist.server_eye.network;

import java.util.List;
import java.util.function.Supplier;

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

        List<? extends String> blacklist = ServerEyeConfig.COMMON.blacklistedMods.get();
        List<? extends String> whitelist = ServerEyeConfig.COMMON.whitelistedPlayers.get();

        boolean caseSensitive = ServerEyeConfig.COMMON.caseSensitive.get();
        boolean logOnly = ServerEyeConfig.COMMON.logOnly.get();

        // --- Whitelist check (username only) ---
        String username = player.getGameProfile().getName();

        boolean isWhitelisted = whitelist.stream()
                .anyMatch(entry -> entry.equalsIgnoreCase(username));

        if (isWhitelisted) {
            System.out.println("[Server-Eye] Player " + username + " is whitelisted.");
            return;
        }

        // --- Blacklist check ---
        for (String mod : payload.mods()) {

            String checkMod = caseSensitive ? mod : mod.toLowerCase();

            boolean isBlacklisted = blacklist.stream()
                    .map(m -> caseSensitive ? m : m.toLowerCase())
                    .anyMatch(m -> m.equals(checkMod));

            if (isBlacklisted) {

                if (logOnly) {
                    System.out.println("[Server-Eye] Forbidden mod detected (logOnly=true): " + mod);
                    return;
                }

                player.connection.disconnect(
                        Component.literal("Forbidden mod detected: " + mod)
                );
                return;
            }
        }
    }
}