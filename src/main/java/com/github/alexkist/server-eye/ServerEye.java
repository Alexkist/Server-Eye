package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.network.ClientModListPayload;
import com.github.alexkist.server_eye.network.ClientModListHandler;
import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.event.RegisterLoginPayloadHandlerEvent;
import net.minecraftforge.network.event.RegisterPayloadHandlersEvent;
import net.minecraftforge.network.PayloadRegistrar;
import net.minecraftforge.network.PayloadFlow;
import net.minecraftforge.fml.config.ModConfig;

@Mod("server-eye")
public class ServerEye {

    public ServerEye() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                ServerEyeConfig.COMMON_SPEC
        );
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.play(
                ClientModListPayload.ID,
                ClientModListPayload::new,
                ClientModListHandler::handle,
                PayloadFlow.CLIENTBOUND
        );
    }

    @SubscribeEvent
    public static void registerLogin(RegisterLoginPayloadHandlerEvent event) {
        event.registrar().login(
                ClientModListPayload.ID,
                ClientModListPayload::new,
                ClientModListHandler::handle
        );
    }
}