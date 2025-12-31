package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.util.ChainedEffectHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class SoulScytheItem extends SwordItem {
    public SoulScytheItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!(entity instanceof PlayerEntity targetPlayer)) return ActionResult.FAIL;
        if (ChainedEffectHandler.isPlayerIncluded(targetPlayer)) {
            ChainedEffectHandler.removePlayer(targetPlayer);
            targetPlayer.removeStatusEffect(ModEffects.CHAINED_EFFECT);
            return ActionResult.success(true);
        } else {
            return ActionResult.FAIL;
        }
    }
}
