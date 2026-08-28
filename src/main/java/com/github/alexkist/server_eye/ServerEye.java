package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.network.ModNetworking;
import com.github.alexkist.server_eye.config.ConfigManager;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod("server_eye")
public class ServerEye {
    public ServerEye(IEventBus modEventBus, ModContainer modContainer) {
        // modContainer.registerConfig(ModConfig.Type.COMMON, ServerEyeConfig.COMMON_SPEC); - Moved this to the ConfigManager
        ConfigManager.load(modContainer);
        modEventBus.addListener(ModNetworking::register);
    }
}