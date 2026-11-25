package com.github.alexkist.server_eye.platform.services;

import net.minecraft.commands.CommandSourceStack;

import java.nio.file.Path;
import java.util.List;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    Path getConfigDirectory();

    List<String> getLoadedModIds();

    boolean canUseServerEyeCommand(CommandSourceStack source);
}
