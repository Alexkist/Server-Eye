package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.config.ServerEyeConfig;
import com.github.alexkist.server_eye.platform.Services;

public final class ServerEyeCommon {

    public static void init() {
        Constants.LOG.info("Loading {} on {}", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());
        ServerEyeConfig.load(Services.PLATFORM.getConfigDirectory());
    }

    private ServerEyeCommon() {}
}