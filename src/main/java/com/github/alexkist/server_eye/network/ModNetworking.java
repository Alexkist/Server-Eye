package com.github.alexkist.server_eye.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("server_eye", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                0,
                ClientModListPayload.class,
                ClientModListPayload::encode,
                ClientModListPayload::decode,
                ClientModListHandler::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}