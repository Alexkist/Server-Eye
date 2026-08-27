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
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "server_eye")
public class commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("servereye")
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
                target.getGameProfile().getName() + " has not sent a mod list yet."
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