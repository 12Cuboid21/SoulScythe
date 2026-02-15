package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.*;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import com.cuboidlabs.soulscythe.util.PersistentModData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public class SoulItemEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDestroyAttempt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity item = (ItemEntity)(Object)this;
        ItemStack theItem = item.getStack();
        if (theItem.getItem() instanceof PlayerSoulItem soulItem) {
            if (source.equals(item.getDamageSources().outOfWorld())) {
                World currentWorld = item.getWorld();
                if (currentWorld instanceof ServerWorld currentServerWorld) {
                    UUID userUuid = theItem.get(ModDataComponentTypes.STORED_PLAYER);
                    if (userUuid == null) {
                        cir.setReturnValue(true);
                        return;
                    }
                    GhostEffectHandler.removePlayerByUuid(userUuid);
                    cir.setReturnValue(true);
                }
            } else {
                cir.setReturnValue(false);
            }
        } else if (theItem.getItem() instanceof SoulScytheItem scytheItem) {
            if (source.equals(item.getDamageSources().outOfWorld())) {
                World currentWorld = item.getWorld();
                if (currentWorld instanceof ServerWorld currentServerWorld) {
                    PersistentModData modDataState = PersistentModData.get(currentServerWorld);
                    modDataState.scytheCrafted = false;
                    modDataState.markDirty();
                    cir.setReturnValue(true);
                }
            } else {
                cir.setReturnValue(false);
            }
        } else if (theItem.getItem() instanceof LightforgedGreatswordItem) {
            if (source.equals(item.getDamageSources().outOfWorld())) {
                World currentWorld = item.getWorld();
                if (currentWorld instanceof ServerWorld currentServerWorld) {
                    PersistentModData modDataState = PersistentModData.get(currentServerWorld);
                    modDataState.greatswordCrafted = false;
                    modDataState.markDirty();
                    cir.setReturnValue(true);
                }
            } else {
                cir.setReturnValue(false);
            }
        } else if (theItem.getItem() instanceof PrismaPiercerItem) {
            cir.setReturnValue(false);
        } else if (theItem.getItem() instanceof HammerOfJusticeItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",
            at = @At("TAIL"))
    private void onDespawnAttempt(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof PlayerSoulItem) {
            ItemEntity self = (ItemEntity)(Object)this;
            self.setNeverDespawn();
        } else if (stack.getItem() instanceof SoulScytheItem) {
            ItemEntity self = (ItemEntity)(Object)this;
            self.setNeverDespawn();
        } else if (stack.getItem() instanceof LightforgedGreatswordItem) {
            ItemEntity self = (ItemEntity)(Object)this;
            self.setNeverDespawn();
        } else if (stack.getItem() instanceof PrismaPiercerItem) {
            ItemEntity self = (ItemEntity)(Object)this;
            self.setNeverDespawn();
        } else if (stack.getItem() instanceof HammerOfJusticeItem) {
            ItemEntity self = (ItemEntity)(Object)this;
            self.setNeverDespawn();
        }
    }
}
