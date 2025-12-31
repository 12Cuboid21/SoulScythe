package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.PlayerSoulItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class SoulItemEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDestroyAttempt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity item = (ItemEntity)(Object)this;
        ItemStack theItem = item.getStack();
        if (theItem.getItem() instanceof PlayerSoulItem soulItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",
            at = @At("TAIL"))
    private void onDespawnAttempt(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof PlayerSoulItem) {
            ItemEntity self = (ItemEntity)(Object)this;
            self.setNeverDespawn();
        }
    }
}
