package com.github.alexkist.server_eye.config;

import java.util.Set;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigManager {

    public static void load() {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.COMMON,
            ServerEyeConfig.COMMON_SPEC
        );
    }

    private static ModConfig findCommonConfig() {
        Set<ModConfig> commonConfigs = ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.COMMON);

        if (commonConfigs == null) {
            return null;
        }

        for (ModConfig config : commonConfigs) {
            if (config.getSpec() == ServerEyeConfig.COMMON_SPEC) {
                return config;
            }
        }

        return null;
    }

    public static boolean reload() {
        ModConfig config = findCommonConfig();

        if (config == null) {
            return false;
        }

        ((CommentedFileConfig) config.getConfigData()).load();
        ServerEyeConfig.COMMON_SPEC.afterReload();
        return true;
    }
}