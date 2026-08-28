package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.config.ConfigManager;
import com.github.alexkist.server_eye.network.ModNetworking;

import net.minecraftforge.fml.common.Mod;

@Mod("server_eye")
public class ServerEye {

    public ServerEye() {
        ConfigManager.load();
        ModNetworking.register();
    }
}