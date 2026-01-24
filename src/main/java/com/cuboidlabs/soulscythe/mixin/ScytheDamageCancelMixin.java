package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.SoulScytheItem;
import com.cuboidlabs.soulscythe.util.ChainedEffectHandler;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Mixin(LivingEntity.class)
public class ScytheDamageCancelMixin {
    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"
            ),
            cancellable = true
    )
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attackerEntity = source.getAttacker();

        LivingEntity target = (LivingEntity)(Object)this;

        if (target.getWorld().isClient) return;

        if(!(attackerEntity instanceof PlayerEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandStack();

        if (!(weapon.getItem() instanceof SoulScytheItem)) {
            if (target instanceof ServerPlayerEntity playerUp) {
                if (ChainedEffectHandler.isPlayerIncluded(playerUp)) {
                    playerUp.setHealth(1F);
                    cir.setReturnValue(false);
                }
            }
            return;
        }

        if(target.getHealth() < 1F) {
            if (target instanceof ServerPlayerEntity player) {
                if (ChainedEffectHandler.isPlayerIncluded(player)) {
                    player.setHealth(10F);
                    cir.setReturnValue(false);
                    banPlayer(player, attacker);
                    return;
                }
                target.getWorld().sendEntityStatus(target, (byte) 35);
                attacker.sendMessage(Text.empty().append("The player ").formatted(Formatting.GOLD)
                        .append(target.getName().getString()).formatted(Formatting.GOLD,Formatting.BOLD)
                        .append(" is ready for the final judgement.").formatted(Formatting.GOLD));
                player.setHealth(1F);
                ChainedEffectHandler.addPlayer(player);
                cir.setReturnValue(false);
            }
        }
    }

    private void banPlayer(ServerPlayerEntity serverPlayer, PlayerEntity attacker) {
        MinecraftServer server = serverPlayer.getServer();
        if (server == null) return;

        ServerWorld serverWorld = serverPlayer.getServerWorld();

        ItemStack drop = new ItemStack(ModItems.PLAYER_SOUL);
        drop.set(ModDataComponentTypes.STORED_PLAYER, serverPlayer.getUuid());
        drop.set(ModDataComponentTypes.STORED_PLAYER_NAME, serverPlayer.getName().getString());
        ItemEntity dropEntity = new ItemEntity(
                serverWorld,
                serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                drop
        );

        dropEntity.setPickupDelay(40);
        dropEntity.setNeverDespawn();
        serverWorld.spawnEntity(dropEntity);

        serverPlayer.getInventory().dropAll();

        for (ServerPlayerEntity sendPlayer: serverWorld.getPlayers()) {
            sendPlayer.sendMessage(
                    Text.empty()
                            .append(Text.literal("Final Judgement").formatted(Formatting.GOLD, Formatting.BOLD))
                            .append(Text.literal(" has been passed for ").formatted(Formatting.GRAY))
                            .append(Text.literal(serverPlayer.getName().getString()).formatted(Formatting.DARK_RED,Formatting.BOLD))
                            .append(Text.literal(". ").formatted(Formatting.GRAY))
                            .append(Text.literal("✦").formatted(Formatting.DARK_RED)),
                    false
            );
        }

        LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
        bolt.setPos(serverPlayer.getX(),serverPlayer.getY(),serverPlayer.getZ());
        bolt.setCosmetic(true);
        serverWorld.spawnEntity(bolt);

        ChainedEffectHandler.removePlayer(serverPlayer);

        GhostEffectHandler.addPlayer(serverPlayer);

        //BannedPlayerEntry entry = new BannedPlayerEntry(serverPlayer.getGameProfile());

        //server.getPlayerManager().getUserBanList().add(entry);

        /*serverPlayer.networkHandler.disconnect(Text.empty()
                .append(Text.literal("Final Judgement").formatted(Formatting.GOLD, Formatting.BOLD))
                .append(Text.literal(" has been passed. ").formatted(Formatting.GRAY))
                .append(Text.literal("✦").formatted(Formatting.DARK_RED)));*/
    }
}
