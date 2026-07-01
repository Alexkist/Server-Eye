package com.github.alexkist.server_eye.config;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerEyeConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = pair.getRight();
        COMMON = pair.getLeft();
    }

    public static class Common {

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedMods;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedPlayers;
        public final ForgeConfigSpec.BooleanValue logOnly;
        public final ForgeConfigSpec.BooleanValue caseSensitive;

        public Common(ForgeConfigSpec.Builder builder) {

            builder.push("server-eye");

            blacklistedMods = builder
                    .comment("Mods that will cause a player to be kicked.")
                    .defineList("blacklistedMods",
                            List.of("xraymod", "cheatengine", "baritone"),
                            o -> o instanceof String);

            whitelistedPlayers = builder
                    .comment("Players who are allowed even if they have forbidden mods.")
                    .defineList("whitelistedPlayers",
                            List.of("Alexkist", "AdminUser"),
                            o -> o instanceof String);

            logOnly = builder
                    .comment("If true, players will NOT be kicked. Only logged.")
                    .define("logOnly", false);

            caseSensitive = builder
                    .comment("If true, mod name matching becomes case-sensitive.")
                    .define("caseSensitive", false);

            builder.pop();
        }
    }
}