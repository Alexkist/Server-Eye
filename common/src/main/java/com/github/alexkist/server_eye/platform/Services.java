package com.github.alexkist.server_eye.platform;

import com.github.alexkist.server_eye.Constants;
import com.github.alexkist.server_eye.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public final class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        T service = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", service, clazz);
        return service;
    }

    private Services() {}
}
