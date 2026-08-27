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
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "server_eye")
public class commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("server_eye")
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("ListMods")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(commands::listMods)))
        );
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