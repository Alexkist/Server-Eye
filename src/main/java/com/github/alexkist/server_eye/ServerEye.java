package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.network.ModNetworking;
import com.github.alexkist.server_eye.config.ServerEyeConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

@Mod("server_eye")
public class ServerEye {

    public ServerEye() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                ServerEyeConfig.COMMON_SPEC
        );

        ModNetworking.register();
    }
}