package com.cuboidlabs.soulscythe.blockentity;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.blockentity.custom.SoulSpawnerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<SoulSpawnerBlockEntity> SOUL_SPAWNER =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(SoulScythe.MOD_ID, "soul_spawner"),
                    FabricBlockEntityTypeBuilder.create(
                            SoulSpawnerBlockEntity::new,
                            ModBlocks.SOUL_SPAWNER
                    ).build()
            );

    public static void register() {}
}