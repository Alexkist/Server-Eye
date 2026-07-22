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

        public Common(ForgeConfigSpec.Builder builder) {

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

            logOnly = builder
                    .comment("If enabled, Players with blacklisted mods will not be kicked, and it will only be logged to the Console.")
                    .define("logOnly", false);

            builder.pop();
        }
    }
}