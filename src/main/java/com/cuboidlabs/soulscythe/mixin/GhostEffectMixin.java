package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class GhostEffectMixin {
    @Shadow
    private ServerPlayerEntity player;

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void blockBlockInteract(ServerPlayerEntity serverPlayer, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (serverPlayer.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void blockEntityInteract(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void blockBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (player.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
            cir.setReturnValue(false);
        }
    }
}
