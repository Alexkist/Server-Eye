package com.github.alexkist.server_eye.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class ConfigManager {

    public static void load(ModContainer modContainer) {
        modContainer.registerConfig(
            ModConfig.Type.COMMON,
            ServerEyeConfig.COMMON_SPEC
        );
    }

}