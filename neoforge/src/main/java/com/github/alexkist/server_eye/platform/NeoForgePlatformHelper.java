package com.github.alexkist.server_eye.platform;

import com.github.alexkist.server_eye.Constants;
import com.github.alexkist.server_eye.platform.services.IPlatformHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.nio.file.Path;
import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

    public static final PermissionNode<Boolean> MODS = new PermissionNode<>(
            Constants.id("mods"),
            PermissionTypes.BOOLEAN,
            (player, uuid, context) -> player != null && player.hasPermissions(4)
    );

    public static final PermissionNode<Boolean> RELOAD = new PermissionNode<>(
            Constants.id("reload"),
            PermissionTypes.BOOLEAN,
            (player, uuid, context) -> player != null && player.hasPermissions(4)
    );

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public List<String> getLoadedModIds() {
        return ModList.get().getMods().stream().map(info -> info.getModId()).toList();
    }

    @Override
    public boolean canUseViewMods(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return PermissionAPI.getPermission(player, MODS);
        }
        return source.hasPermission(2); // Console or command blocks
    }

    @Override
    public boolean canUseReload(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return PermissionAPI.getPermission(player, RELOAD);
        }
        return source.hasPermission(2); // Console or command blocks
    }
}
