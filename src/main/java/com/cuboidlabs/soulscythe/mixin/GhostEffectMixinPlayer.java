package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class GhostEffectMixinPlayer {
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attackCancelling(Entity target, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        if (player.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
            ci.cancel();
        }
    }
}
