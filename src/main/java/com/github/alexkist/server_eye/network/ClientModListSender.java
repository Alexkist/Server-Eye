package com.github.alexkist.server_eye.network;

import java.util.List;
import java.util.stream.Collectors;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "server_eye", value = Dist.CLIENT)
public class ClientModListSender {

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        List<String> mods = ModList.get().getMods().stream()
                .map(info -> info.getModId())
                .collect(Collectors.toList());
        
        PacketDistributor.sendToServer(new ClientModListPayload(mods));
    }
}