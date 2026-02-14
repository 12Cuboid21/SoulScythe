package com.cuboidlabs.soulscythe.block;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.block.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.swing.text.html.BlockView;

public class ModBlocks {
    public static final Block LIGHT_FORGE = registerBlock("light_forge", new LightForgeBlock(AbstractBlock.Settings.create().strength(9999f).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));
    public static final Block ANVIL_OF_JUSTICE = registerBlock("anvil_of_justice", new AnvilOfJusticeBlock(AbstractBlock.Settings.create().strength(9999f).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));
    public static final Block PRISMA_HEART = registerBlock("prisma_heart", new PrismaHeartBlock(AbstractBlock.Settings.create().strength(9999f).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK).emissiveLighting(Blocks::always).luminance(value -> 10)));
    public static final Block REINCARNATION_BLOCK = registerBlock("reincarnation_block", new ReincarnationBlock(AbstractBlock.Settings.create().strength(9999f).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));
    public static final Block SOUL_VAULT = registerBlock("soul_vault", new SoulVaultBlock(AbstractBlock.Settings.create().strength(9999f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block SOUL_SPAWNER = registerBlock("soul_spawner", new SoulSpawnerBlock(AbstractBlock.Settings.create().strength(9999f).requiresTool().sounds(BlockSoundGroup.STONE)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(SoulScythe.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(SoulScythe.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        SoulScythe.LOGGER.info("Registering Mod Blocks for" + SoulScythe.MOD_ID);

        /*ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(REINCARNATION_BLOCK);
        });*/
    }
}
