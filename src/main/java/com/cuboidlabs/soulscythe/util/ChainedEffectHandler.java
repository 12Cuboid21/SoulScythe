package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

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

            spawnFlashCircle(world, player);

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

    public static void spawnFlashCircle(ServerWorld world, PlayerEntity player) {
        double radius = 1;
        int points = 32;

        double cx = player.getX();
        double cy = player.getY() + 1.0;
        double cz = player.getZ();

        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;

            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;

            // Yellow dust accent
            world.spawnParticles(
                    new DustParticleEffect(
                            new Vector3f(1.0f, 1.0f, 0.2f), // yellow
                            1.5f
                    ),
                    x, cy, z,
                    2,
                    0, 0, 0,
                    0
            );
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
