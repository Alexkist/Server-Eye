package com.github.alexkist.server_eye.client;

import com.github.alexkist.server_eye.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ServerEyeNeoForgeClient {

    public ServerEyeNeoForgeClient() {
        NeoForge.EVENT_BUS.addListener(ServerEyeNeoForgeClient::onLoggingIn);
    }

    private static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        PacketDistributor.sendToServer(ClientModListCollector.createPayload());
    }
}
