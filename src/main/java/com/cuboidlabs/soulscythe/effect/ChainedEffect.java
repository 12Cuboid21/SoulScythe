package com.cuboidlabs.soulscythe.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ChainedEffect extends StatusEffect {
    public ChainedEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFFFF00);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            // Cancel jump input
            player.setJumping(false);

            // Optional: cancel sprinting
            player.setSprinting(false);
        }
        return true; // tick every game tick
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // runs every tick
    }
}
