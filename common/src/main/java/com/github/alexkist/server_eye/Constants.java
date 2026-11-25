package com.github.alexkist.server_eye;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {

    public static final String MOD_ID = "server_eye";
    public static final String MOD_NAME = "Server Eye";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final String NETWORK_VERSION = "1";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private Constants() {}
}