package com.github.alexkist.server_eye.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "server_eye", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {

    private static ModConfig commonConfig;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == ServerEyeConfig.COMMON_SPEC) {
            commonConfig = event.getConfig();
        }
    }

    public static boolean reload() {
        if (commonConfig == null) {
            return false;
        }
        ((CommentedFileConfig) commonConfig.getConfigData()).load();
        return true;
    }
}