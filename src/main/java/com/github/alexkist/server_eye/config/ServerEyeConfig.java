package com.github.alexkist.server_eye.config;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;



public class ServerEyeConfig {

    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = pair.getRight();
        COMMON = pair.getLeft();
    }

    public static class Common {
        
        public static final ModConfigSpec.ConfigValue<List<? extends String>> blacklistedMods;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> whitelistedPlayers;
        public static final ModConfigSpec.IntValue timeoutSeconds;
        public static final ModConfigSpec.BooleanValue logOnly;

        public Common(ModConfigSpec.Builder builder) {

            builder.push("server_eye");

            blacklistedMods = builder
                    .comment("Add modIds here to blacklist them. Players with these mods will be kicked.")
                    .defineList("blacklistedMods",
                            List.of(),
                            o -> o instanceof String);

            whitelistedPlayers = builder
                    .comment("Add player names here to whitelist them. Whitelisted players will not be kicked, even if they have blacklisted mods.")
                    .defineList("whitelistedPlayers",
                            List.of(),
                            o -> o instanceof String);

            timeoutSeconds = builder
                    .comment("How many seconds to wait for a player's mod list before kicking them. Set to 0 to disable this check.")
                    .defineInRange("timeoutSeconds", 10, 0, 300);

            logOnly = builder
                    .comment("If enabled, Players with blacklisted mods will not be kicked, and it will only be logged to the Console.")
                    .define("logOnly", false);
            
            builder.pop();
        }
    }
}