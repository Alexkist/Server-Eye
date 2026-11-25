package com.github.alexkist.server_eye.network;

import com.github.alexkist.server_eye.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

/**
 * Client -> server: the client's mod ids.
 * Pure vanilla classes, so it lives in common. Each loader only registers it.
 */
public record ClientModListPayload(List<String> mods) implements CustomPacketPayload {

    public static final Type<ClientModListPayload> TYPE = new Type<>(Constants.id("mod_list"));

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