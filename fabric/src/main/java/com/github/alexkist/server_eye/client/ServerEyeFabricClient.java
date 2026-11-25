package com.github.alexkist.server_eye.client;

import com.github.alexkist.server_eye.network.ClientModListPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ServerEyeFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // Only send if the server has Server Eye (avoids errors on vanilla servers)
            if (ClientPlayNetworking.canSend(ClientModListPayload.TYPE)) {
                ClientPlayNetworking.send(ClientModListCollector.createPayload());
            }
        });
    }
}
