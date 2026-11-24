package com.github.alexkist.server_eye.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.alexkist.server_eye.Constants;
import com.github.alexkist.server_eye.config.ServerEyeConfig;
import com.github.alexkist.server_eye.network.ClientModListPayload;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ModListVerifier {

    public static void process(ClientModListPayload payload, ServerPlayer player) {
        ModListTimeoutHandler.markReceived(player);
        PlayerModListStore.store(player, payload.mods());

        ServerEyeConfig.Values config = ServerEyeConfig.get();
        String username = player.getGameProfile().getName();

        boolean isWhitelisted = config.whitelistedPlayers.stream()
                .anyMatch(entry -> entry.equalsIgnoreCase(username));
        if (isWhitelisted) {
            Constants.LOG.info("{} is whitelisted, and has skipped mod checks.", username);
            return;
        }

        Set<String> blacklist = config.blacklistedMods.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> forbiddenMods = new ArrayList<>();
        for (String mod : payload.mods()) {
            if (blacklist.contains(mod.toLowerCase())) {
                forbiddenMods.add(mod);
            }
        }

        if (forbiddenMods.isEmpty()) {
            return;
        }

        Constants.LOG.info("{} has blacklisted mod(s): {}", username, String.join(", ", forbiddenMods));

        if (!config.logOnly) {
            player.connection.disconnect(Component.literal("""
                    Server Eye
                    One or more of your installed mods are are not allowed on the Server.
                    Please remove all mods listed below to join:
                    %s""".formatted(String.join("\n", forbiddenMods))));
        }
    }

    private ModListVerifier() {}
}
