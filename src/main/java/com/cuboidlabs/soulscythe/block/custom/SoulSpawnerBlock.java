package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.blockentity.ModBlockEntities;
import com.cuboidlabs.soulscythe.blockentity.custom.SoulSpawnerBlockEntity;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SoulSpawnerBlock extends BlockWithEntity implements BlockEntityProvider {

    public SoulSpawnerBlock(Settings settings) {
        super(settings);
    }
    public static final MapCodec<SoulSpawnerBlock> CODEC = SoulSpawnerBlock.createCodec(SoulSpawnerBlock::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulSpawnerBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof SoulSpawnerBlockEntity spawner)) return ActionResult.FAIL;

        if (player.getMainHandStack().isOf(Items.DIAMOND)) {
            player.getMainHandStack().decrement(1);
            spawner.activate();
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {

        if (world.isClient) return null;

        return type == ModBlockEntities.SOUL_SPAWNER
                ? (w, p, s, be) -> SoulSpawnerBlockEntity.tick(
                (ServerWorld) w, p, s, (SoulSpawnerBlockEntity) be)
                : null;
    }
}