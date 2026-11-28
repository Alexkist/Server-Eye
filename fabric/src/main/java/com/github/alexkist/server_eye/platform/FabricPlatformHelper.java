package com.github.alexkist.server_eye.platform;

import java.nio.file.Path;
import java.util.List;

import com.github.alexkist.server_eye.platform.services.IPlatformHelper;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public List<String> getLoadedModIds() {
        return FabricLoader.getInstance().getAllMods().stream()
                .map(mod -> mod.getMetadata().getId())
                .toList();
    }

    @Override
    public boolean canUseViewMods(CommandSourceStack source) {
        return Permissions.check(source, "server_eye.mods", source.getEntity() == null ? 2 : 4);
    }

    @Override
    public boolean canUseReload(CommandSourceStack source) {
        return Permissions.check(source, "server_eye.reload", source.getEntity() == null ? 2 : 4);
    }
}
