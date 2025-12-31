package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ChainedMovementMixin {

    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void soulscythe$freezeIfChained(Vec3d movementInput, CallbackInfo ci) {

        if ((Object) this instanceof PlayerEntity player && player.hasStatusEffect(ModEffects.CHAINED_EFFECT)) {

            player.setVelocity(0, 0, 0);
            player.fallDistance = 0;

            ci.cancel(); // HARD FREEZE
        }
    }
}
