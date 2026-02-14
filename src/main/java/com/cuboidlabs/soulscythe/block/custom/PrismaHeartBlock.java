package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.LifeGemItem;
import com.cuboidlabs.soulscythe.item.custom.PrismariteShardItem;
import com.cuboidlabs.soulscythe.util.PersistentModData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class PrismaHeartBlock extends Block {
    public PrismaHeartBlock(Settings settings) {
        super(settings);
    }
    private Set<BlockPos> bookAquired = new HashSet<>();

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            ItemStack handStack = player.getMainHandStack();
            PersistentModData modDataState = PersistentModData.get(serverWorld);
            if (handStack.getItem() instanceof PrismariteShardItem) {
                if (modDataState.piercerCrafted) {
                    player.sendMessage(Text.of("The Prismapiercer has already been crafted."));
                    return ActionResult.success(true);
                }
                handStack.decrement(1);
                player.getInventory().insertStack(new ItemStack(ModItems.PRISMAPIERCER));
                player.sendMessage(Text.of("The Prismapiercer has been crafted."));
                LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
                bolt.setPos(pos.getX(),pos.getY(),pos.getZ());
                bolt.setCosmetic(true);
                serverWorld.spawnEntity(bolt);
                modDataState.piercerCrafted = true;
                modDataState.piercerOwner = player.getUuid();
                modDataState.markDirty();
                return ActionResult.success(true);
            } else {
                player.sendMessage(Text.of("You are missing the gem in the spectrum."));
                return ActionResult.success(true);
            }
        }
        return ActionResult.FAIL;
    }
}
