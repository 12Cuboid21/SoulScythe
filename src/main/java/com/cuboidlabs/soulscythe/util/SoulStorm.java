package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.SoulScythe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.UUID;

public class SoulStorm {
    public static int timeLeft = -1; // -1 = inactive
    public static int cooldown = 0;
    private static int ticks = 0;

    public static void tick(MinecraftServer server) {
        ticks++;
        if (ticks < 20) return; // once per second
        ticks = 0;

        if (cooldown > 0) {
            cooldown--;
            /*server.getPlayerManager().getPlayer(state.activatorPlayer).sendMessage(Text.of("The storm is on cooldown (" + cooldown + "s)"));
            return;*/
        }

        if (timeLeft < 0) return;

        ServerWorld overworld = server.getOverworld();
        SoulStormState state = SoulStormState.get(overworld);

        //System.out.println("SS Cooldown: " + cooldown);

        /*if (!state.stormActive) {
            timeLeft = -1;
            return;
        }*/

        if (timeLeft > 0) {
            timeLeft--;

            ServerPlayerEntity activator =
                    server.getPlayerManager().getPlayer(state.activatorPlayer);

            if (activator != null) {
                applyStormEffects(activator);
            }

        } else if (timeLeft == 0) {
            // Storm ended
            state.stormActive = false;
            state.markDirty();
            SoulScythe.sync(overworld);

            ServerPlayerEntity activator =
                    server.getPlayerManager().getPlayer(state.activatorPlayer);

            if (activator != null) {
                clearStormEffects(activator);
            }
            cooldown = 300;
            //System.out.println("start SS Cooldown: " + cooldown);
            for (ServerPlayerEntity onePlayer : overworld.getPlayers()) {
                onePlayer.sendMessage(Text.empty().append("The storm has been suppresed").formatted(Formatting.GREEN));
            }

            timeLeft = -1;
        }
    }

    private static void applyStormEffects(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED, 40, 1, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HEALTH_BOOST, 40, 2, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HASTE, 40, 1, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION, 40, 000, false, false, true));
    }

    private static void clearStormEffects(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.SPEED);
        player.removeStatusEffect(StatusEffects.HEALTH_BOOST);
        player.removeStatusEffect(StatusEffects.HASTE);
        player.removeStatusEffect(StatusEffects.REGENERATION);
    }
}
