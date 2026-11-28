package com.github.alexkist.server_eye.platform.services;

import java.nio.file.Path;
import java.util.List;

import net.minecraft.commands.CommandSourceStack;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    Path getConfigDirectory();

    List<String> getLoadedModIds();

    boolean canUseViewMods(CommandSourceStack source);

    boolean canUseReload(CommandSourceStack source);
}
