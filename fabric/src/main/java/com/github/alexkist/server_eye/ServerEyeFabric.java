package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.network.ClientModListPayload;
import com.github.alexkist.server_eye.server.ModListTimeoutHandler;
import com.github.alexkist.server_eye.server.ModListVerifier;
import com.github.alexkist.server_eye.server.PlayerModListStore;
import com.github.alexkist.server_eye.server.ServerEyeCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerEyeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerEyeCommon.init();

        // The payload type must be registered on both sides
        PayloadTypeRegistry.playC2S().register(ClientModListPayload.TYPE, ClientModListPayload.STREAM_CODEC);

        // Fabric already runs this receiver on the server thread
        ServerPlayNetworking.registerGlobalReceiver(ClientModListPayload.TYPE,
                (payload, context) -> ModListVerifier.process(payload, context.player()));

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> ServerEyeCommands.register(dispatcher));

        ServerTickEvents.END_SERVER_TICK.register(ModListTimeoutHandler::tick);

        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> ModListTimeoutHandler.onPlayerLoggedIn(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ModListTimeoutHandler.onPlayerLoggedOut(handler.getPlayer());
            PlayerModListStore.onPlayerLoggedOut(handler.getPlayer());
        });
    }
}
