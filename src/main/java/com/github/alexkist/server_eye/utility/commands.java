package com.github.alexkist.server_eye.utility;

import java.util.List;

import com.github.alexkist.server_eye.config.ConfigManager;
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
            Commands.literal("server_eye")
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("reload")
                    .executes(commands::reloadConfig))
                .then(Commands.literal("viewMods")
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

    private static int reloadConfig(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        boolean success = ConfigManager.reload();

        if (!success) {
            source.sendFailure(Component.literal("Failed to reload Config."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Config reloaded!"), true);

        // --- Kick Players --
        // This is just a safety precaution :)
        // Leaving this out for now as I think it messes with the Server a little
        //for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
        //    player.connection.disconnect(Component.literal("""
        //        Server Eye
        //        Config has been reloaded, please rejoin!"""
        //    ));
        //}
        System.out.println("[Server Eye] Config reloaded");

        return 1;
    }
}