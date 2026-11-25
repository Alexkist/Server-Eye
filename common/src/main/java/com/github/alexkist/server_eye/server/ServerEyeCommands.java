package com.github.alexkist.server_eye.server;

import com.github.alexkist.server_eye.config.ServerEyeConfig;
import com.github.alexkist.server_eye.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class ServerEyeCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("server_eye")
                        .requires(Services.PLATFORM::canUseServerEyeCommand)
                        .then(Commands.literal("viewMods")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ServerEyeCommands::listMods)))
                        .then(Commands.literal("reload")
                                .executes(ServerEyeCommands::reload))
        );
    }

    private static int listMods(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        List<String> mods = PlayerModListStore.get(target);
        String name = target.getGameProfile().getName();

        if (mods == null) {
            source.sendFailure(Component.literal(name + " has not yet sent their mod list to the server"));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(
                name + " (" + mods.size() + " mods): " + String.join(", ", mods)), false);
        return mods.size();
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        ServerEyeConfig.reload();
        context.getSource().sendSuccess(() -> Component.literal("Server Eye config reloaded"), true);
        return 1;
    }

    private ServerEyeCommands() {}
}
