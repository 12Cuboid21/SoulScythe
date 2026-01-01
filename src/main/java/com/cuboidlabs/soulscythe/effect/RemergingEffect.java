package com.cuboidlabs.soulscythe.effect;

import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class RemergingEffect extends StatusEffect {
    protected RemergingEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
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
            GhostEffectHandler.addPlayer(serverPlayer);
        }
        super.onEntityRemoval(entity, amplifier, reason);
    }
}
