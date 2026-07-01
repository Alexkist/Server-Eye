package com.github.alexkist.server_eye.network;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;

public class ClientModListPayload {

    private final List<String> mods;

    public ClientModListPayload(List<String> mods) {
        this.mods = mods;
    }

    public List<String> mods() {
        return mods;
    }

    public static void encode(ClientModListPayload payload, FriendlyByteBuf buf) {
        buf.writeCollection(payload.mods, FriendlyByteBuf::writeUtf);
    }

    public static ClientModListPayload decode(FriendlyByteBuf buf) {
        return new ClientModListPayload(buf.readList(FriendlyByteBuf::readUtf));
    }
}