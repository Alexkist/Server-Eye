package com.github.alexkist.server_eye.network;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "server_eye", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientModListSender {

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        List<String> mods = ModList.get().getMods().stream()
                .map(info -> info.getModId())
                .collect(Collectors.toList());

        ModNetworking.CHANNEL.sendToServer(new ClientModListPayload(mods));
    }
}