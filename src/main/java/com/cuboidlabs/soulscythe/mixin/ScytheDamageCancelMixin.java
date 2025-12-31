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

        if (!(weapon.getItem() instanceof SoulScytheItem)) return;

        if(target.getHealth() < 1F) {
            if (target instanceof ServerPlayerEntity player) {
                if (ChainedEffectHandler.isPlayerIncluded(player)) {/*cir.setReturnValue(false);*/ banPlayer(player, attacker); return;}
                target.getWorld().sendEntityStatus(target, (byte) 35);
                attacker.sendMessage(Text.literal(("The player " + target.getName().getString() + " is ready for the final judgement.")));
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

        attacker.sendMessage(
                Text.empty()
                        .append(Text.literal("Final Judgement").formatted(Formatting.GOLD, Formatting.BOLD))
                        .append(Text.literal(" has been passed. ").formatted(Formatting.GRAY))
                        .append(Text.literal("✦").formatted(Formatting.DARK_RED)),
                false
        );

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
