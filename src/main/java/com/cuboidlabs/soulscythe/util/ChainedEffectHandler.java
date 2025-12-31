package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChainedEffectHandler {
    private static final Set<UUID> playersWithEffect = new HashSet<>();

    public static void tick(ServerWorld world) {
        if (world.isClient) return;
        for (UUID uuid : playersWithEffect) {
            PlayerEntity player = world.getPlayerByUuid(uuid);
            if (player == null) continue;

            if (!player.hasStatusEffect(ModEffects.CHAINED_EFFECT)) {
                player.addStatusEffect(
                        new StatusEffectInstance(ModEffects.CHAINED_EFFECT,
                                60,
                                0,
                                false,
                                false,
                                false)
                );
            }
        }
    }

    // Call this when you want to “lock” a player with the effect
    public static void addPlayer(PlayerEntity player) {
        playersWithEffect.add(player.getUuid());
    }

    // Call this if you want to remove them
    public static void removePlayer(PlayerEntity player) {
        playersWithEffect.remove(player.getUuid());
        player.removeStatusEffect(ModEffects.CHAINED_EFFECT);
    }

    public static boolean isPlayerIncluded(PlayerEntity player) {
        return playersWithEffect.contains(player.getUuid());
    }
}
