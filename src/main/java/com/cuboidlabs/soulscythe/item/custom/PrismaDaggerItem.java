package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.util.PrismapiercerAbilityCooldownHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.List;

public class PrismaDaggerItem extends SwordItem {
    public PrismaDaggerItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof ServerPlayerEntity serverPlayerTarget) {
            if (serverPlayerTarget.getInventory().contains(new ItemStack(ModItems.PLAYER_SOUL))) {
                int playerSoulSlot = serverPlayerTarget.getInventory().getSlotWithStack(new ItemStack(ModItems.PLAYER_SOUL));
                ItemStack playerSoulStack = serverPlayerTarget.getInventory().getStack(playerSoulSlot);
                if (attacker instanceof ServerPlayerEntity serverAttacker) {
                    serverAttacker.getInventory().insertStack(playerSoulStack);
                    serverPlayerTarget.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.BLINDNESS,
                                    60,
                                    3,
                                    false,
                                    false,
                                    false)
                    );
                    serverPlayerTarget.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.SLOWNESS,
                                    60,
                                    2,
                                    false,
                                    false,
                                    false)
                    );
                    playerSoulStack.decrement(1);
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
