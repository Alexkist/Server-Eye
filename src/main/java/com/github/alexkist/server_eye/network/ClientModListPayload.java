package com.github.alexkist.server_eye.network;

import java.util.List;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientModListPayload(List<String> mods) implements CustomPacketPayload {

    public static final Type<ClientModListPayload> TYPE =
        new Type<> (ResourceLocation.fromNamespaceAndPath("server_eye", "mod_list"));
    
    public static final StreamCodec<ByteBuf, ClientModListPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            ClientModListPayload::mods,
            ClientModListPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}