package com.cuboidlabs.soulscythe.component;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<UUID> STORED_PLAYER = register("stored_player", builder -> builder.codec(Uuids.CODEC));
    public static final ComponentType<String> STORED_PLAYER_NAME = register("stored_player_name",stringBuilder -> stringBuilder.codec(Codec.STRING));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(SoulScythe.MOD_ID,name),builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        SoulScythe.LOGGER.info("Registering mod items for: " + SoulScythe.MOD_ID);
    }
}
