package com.github.alexkist.server_eye.client;

import com.github.alexkist.server_eye.network.ClientModListPayload;
import com.github.alexkist.server_eye.platform.Services;

public final class ClientModListCollector {

    public static ClientModListPayload createPayload() {
        return new ClientModListPayload(Services.PLATFORM.getLoadedModIds());
    }

    private ClientModListCollector() {}
}