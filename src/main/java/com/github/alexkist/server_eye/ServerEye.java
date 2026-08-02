package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.network.ModNetworking;
import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod("server_eye")
public class ServerEye {
    public ServerEye(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ServerEyeConfig.COMMON_SPEC);
        modEventBus.addListener(ModNetworking::register);
    }
}