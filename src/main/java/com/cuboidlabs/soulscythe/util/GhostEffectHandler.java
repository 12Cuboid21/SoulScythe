package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GhostEffectHandler {
    private static final Set<UUID> ghostedPlayers = new HashSet<>();
    private static final Set<UUID> toRevive = new HashSet<>();
    public static void tick(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players) {
            if (player.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
                if (player.getAbilities().invulnerable == false) {player.getAbilities().invulnerable = true; player.sendAbilitiesUpdate();}
                if (player.getAbilities().allowFlying == false) {player.getAbilities().allowFlying = true; player.sendAbilitiesUpdate();}
                if (player.getAbilities().flying == false) {player.getAbilities().flying = true; player.sendAbilitiesUpdate();}
            } else if (!player.interactionManager.getGameMode().isSurvivalLike()) {
                continue;
            } else {
                if (player.getAbilities().invulnerable == true) {player.getAbilities().invulnerable = false; player.sendAbilitiesUpdate();}
                if (player.getAbilities().allowFlying == true) {player.getAbilities().allowFlying = false; player.sendAbilitiesUpdate();}
                if (player.getAbilities().flying == true) {player.getAbilities().flying = false; player.sendAbilitiesUpdate();}
            }
        }
        for (UUID ghostedPlayer : ghostedPlayers) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(ghostedPlayer);
            if (serverPlayer == null) return;
            if (!serverPlayer.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
                serverPlayer.addStatusEffect(new StatusEffectInstance(ModEffects.GHOST_EFFECT,-1, 0, true, false, false));
            }
        }
        for (UUID revive : toRevive) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(revive);
            if (serverPlayer == null) return;
            if (serverPlayer.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
                serverPlayer.removeStatusEffect(ModEffects.GHOST_EFFECT);
            }
            toRevive.remove(revive);
        }
    }

    public static void addPlayer(ServerPlayerEntity player) {
        ghostedPlayers.add(player.getUuid());
    }

    public static void removePlayer(ServerPlayerEntity player) {
        ghostedPlayers.remove(player.getUuid());
    }

    public static void removePlayerByUuid(UUID player) {
        ghostedPlayers.remove(player);
        toRevive.add(player);
    }

    public static void isPlayerIncluded(ServerPlayerEntity player) {
        ghostedPlayers.contains(player.getUuid());
    }
}
