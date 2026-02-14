package com.cuboidlabs.soulscythe.datagen;

import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.REINCARNATION_BLOCK);
        //blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOUL_VAULT);
        //blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOUL_SPAWNER);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //itemModelGenerator.register(ModItems.SOUL_SCYTHE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PLAYER_SOUL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SHADER_DEBUG_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIFE_GEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.PRISMARITE_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.PRISMA_DAGGER, Models.GENERATED);
        itemModelGenerator.register(ModItems.SOUL_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.HAMMER_BLUEPRINT, Models.GENERATED);
    }
}
