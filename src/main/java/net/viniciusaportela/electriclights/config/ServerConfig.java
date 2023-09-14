package net.viniciusaportela.electriclights.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> ELECTRIC_BLOCK_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> ELECTRIC_LIGHT_COST;

    static {
        BUILDER.push("Configs for Electric Lights");

        ELECTRIC_BLOCK_RANGE = BUILDER.comment("Range to reach lights around the electric block").define("Electric Block Range", 16);
        ELECTRIC_LIGHT_COST = BUILDER.comment("The cost in FE that will be spend per tick").define("Electric Light Cost", 20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
