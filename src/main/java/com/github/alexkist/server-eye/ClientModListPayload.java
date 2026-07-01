package com.github.alexkist.server_eye.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.ICustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ClientModListPayload(List<String> mods) implements ICustomPacketPayload {

    public static final ResourceLocation ID =
            new ResourceLocation("server-eye", "client_modlist");

    public ClientModListPayload(FriendlyByteBuf buf) {
        this(buf.readList(FriendlyByteBuf::readUtf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(mods, FriendlyByteBuf::writeUtf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}