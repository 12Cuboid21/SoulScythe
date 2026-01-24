package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.LifeGemItem;
import com.cuboidlabs.soulscythe.item.custom.SoulShardItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulVaultBlock extends Block {
    private int soulShardsUsed = 0;
    public SoulVaultBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.FAIL;
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof SoulShardItem) {
            stack.decrement(1);
            soulShardsUsed += 1;
            player.sendMessage(Text.empty().append("(" + soulShardsUsed + "/3) Soul Shards have been merged.").formatted(Formatting.GOLD));
            if (soulShardsUsed < 3) world.playSound(player, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
        }
        if (soulShardsUsed >= 3) {
            player.getInventory().insertStack(new ItemStack(ModItems.LIFE_GEM));
            player.sendMessage(Text.empty().append("All 3 Soul Shards have merged together and created a Life Gem").formatted(Formatting.GOLD));
            soulShardsUsed = 0;
            world.playSound(player, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS);
        }
        return ActionResult.success(true);
    }
}
