package com.github.alexkist.server_eye.network;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "server_eye")
public class PlayerModListStore {

    public static final Map<UUID, List<String>> modLists = new ConcurrentHashMap<>();

    public static void store(ServerPlayer player, List<String> mods) {
        modLists.put(player.getUUID(), mods);
    }

    public static List<String> get(ServerPlayer player) {
        return modLists.get(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            modLists.remove(player.getUUID());
        }
    }
}