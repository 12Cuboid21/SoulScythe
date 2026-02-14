package com.cuboidlabs.soulscythe.blockentity.custom;

import com.cuboidlabs.soulscythe.blockentity.ModBlockEntities;
import com.cuboidlabs.soulscythe.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SoulSpawnerBlockEntity extends BlockEntity {

    private int attackWave = 0;
    private boolean active = false;
    private final Set<UUID> enemies = new HashSet<>();

    public SoulSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUL_SPAWNER, pos, state);
    }

    public static void tick(ServerWorld world, BlockPos pos, BlockState state, SoulSpawnerBlockEntity be) {
        if (!be.active) return;

        // Remove dead enemies safely
        be.enemies.removeIf(uuid -> {
            Entity e = world.getEntity(uuid);
            return !(e instanceof LivingEntity living) || !living.isAlive();
        });

        // If enemies still alive, wait
        if (!be.enemies.isEmpty()) return;

        be.attackWave++;

        if (be.attackWave > 5) {
            be.finish(world, pos);
            return;
        }

        be.spawnWave(world, pos);
        be.markDirty();
    }

    private void spawnWave(ServerWorld world, BlockPos pos) {
        int count = attackWave == 5 ? 3 : 5;

        for (int i = 0; i < count; i++) {
            LivingEntity entity = switch (attackWave) {
                case 1 -> new HuskEntity(EntityType.HUSK, world);
                case 2 -> new BoggedEntity(EntityType.BOGGED, world);
                case 3 -> new CaveSpiderEntity(EntityType.CAVE_SPIDER, world);
                case 4 -> new VindicatorEntity(EntityType.VINDICATOR, world);
                case 5 -> new VexEntity(EntityType.VEX, world);
                default -> null;
            };

            if (entity == null) continue;

            entity.refreshPositionAndAngles(
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    world.random.nextFloat() * 360f,
                    0
            );

            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));

            if (!(entity instanceof VexEntity) && !(entity instanceof CaveSpiderEntity) && !(entity instanceof VindicatorEntity)) {
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            }

            world.spawnEntity(entity);
            enemies.add(entity.getUuid());
        }
    }

    private void finish(ServerWorld world, BlockPos pos) {
        world.spawnEntity(new ItemEntity(world,
                pos.getX() + 0.5,
                pos.getY() + 1,
                pos.getZ() + 0.5,
                new ItemStack(ModItems.SOUL_SHARD)));

        active = false;
        attackWave = 0;
    }

    public void activate() {
        if (!active) {
            active = true;
            attackWave = 0;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);

        nbt.putInt("Wave", attackWave);
        nbt.putBoolean("Active", active);

        NbtList list = new NbtList();
        for (UUID uuid : enemies) {
            list.add(NbtHelper.fromUuid(uuid));
        }
        nbt.put("Enemies", list);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        attackWave = nbt.getInt("Wave");
        active = nbt.getBoolean("Active");

        enemies.clear();
        NbtList list = nbt.getList("Enemies", NbtElement.INT_ARRAY_TYPE);
        for (NbtElement e : list) {
            enemies.add(NbtHelper.toUuid(e));
        }
    }
}