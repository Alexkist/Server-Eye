package com.github.alexkist.server_eye.utility;

import java.util.List;

import com.github.alexkist.server_eye.network.PlayerModListStore;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

@EventBusSubscriber(modid = "server_eye")
public class commands {

    public static final PermissionNode<Boolean> MODS = new PermissionNode<>(
        ResourceLocation.fromNamespaceAndPath("server_eye", "mods"),
        PermissionTypes.BOOLEAN,
        (player, playerUUID, context) -> player != null && player.hasPermissions(4)
    );

    @SubscribeEvent
    public static void onGatherNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(MODS);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("server_eye")
                .requires(source -> hasPermission(source, MODS)) // Leaving this like this for now, until I find a better solution
                .then(Commands.literal("viewMods")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(commands::listMods)))
        );
    }

    private static boolean hasPermission(CommandSourceStack source, PermissionNode<Boolean> node) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return PermissionAPI.getPermission(player, node);
        }
        return source.hasPermission(2); // Leaving this so you can still use Commands over the Console. Might add it to the Config.
    }

    private static int listMods(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer target = EntityArgument.getPlayer(context, "player");

        List<String> mods = PlayerModListStore.get(target);

        if (mods == null) {
            source.sendFailure(Component.literal(
                target.getGameProfile().getName() + " has not yet sent there mod list to the Server"
            ));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(
            target.getGameProfile().getName() + " (" + mods.size() + " mods): "
                + String.join(", ", mods)
        ), false);

        return mods.size();
    }
}