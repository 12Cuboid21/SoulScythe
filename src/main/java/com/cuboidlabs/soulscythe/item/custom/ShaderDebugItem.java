package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.SoulScytheClient;
import com.cuboidlabs.soulscythe.util.SoulStorm;
import com.cuboidlabs.soulscythe.util.SoulStormState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ShaderDebugItem extends Item {
    public ShaderDebugItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.fail(stack);
        SoulStormState state = SoulStormState.get((ServerWorld) world);
        state.stormActive = !state.stormActive;
        state.activatorPlayer = user.getUuid();
        state.markDirty();
        SoulScythe.sync((ServerWorld) world);
        //SoulScytheClient.color.set((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
        return TypedActionResult.success(stack, true);
    }
}
