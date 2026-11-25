package com.github.alexkist.server_eye;

import com.github.alexkist.server_eye.network.ClientModListPayload;
import com.github.alexkist.server_eye.platform.NeoForgePlatformHelper;
import com.github.alexkist.server_eye.server.ModListTimeoutHandler;
import com.github.alexkist.server_eye.server.ModListVerifier;
import com.github.alexkist.server_eye.server.PlayerModListStore;
import com.github.alexkist.server_eye.server.ServerEyeCommands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;

@Mod(Constants.MOD_ID)
public class ServerEyeNeoForge {

    public ServerEyeNeoForge(IEventBus modEventBus) {
        ServerEyeCommon.init();

        // Mod bus
        modEventBus.addListener(ServerEyeNeoForge::registerPayloads);

        // Game bus: forward to common code
        NeoForge.EVENT_BUS.addListener((PermissionGatherEvent.Nodes e) -> e.addNodes(NeoForgePlatformHelper.MODS));
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> ServerEyeCommands.register(e.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post e) -> ModListTimeoutHandler.tick(e.getServer()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                ModListTimeoutHandler.onPlayerLoggedIn(player);
            }
        });
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                ModListTimeoutHandler.onPlayerLoggedOut(player);
                PlayerModListStore.onPlayerLoggedOut(player);
            }
        });
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(Constants.NETWORK_VERSION)
                .optional() // lets vanilla / Server-Eye-less clients join so the timeout can kick them with a clear message
                .playToServer(ClientModListPayload.TYPE, ClientModListPayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> {
                            if (context.player() instanceof ServerPlayer player) {
                                ModListVerifier.process(payload, player);
                            }
                        }));
    }
}
