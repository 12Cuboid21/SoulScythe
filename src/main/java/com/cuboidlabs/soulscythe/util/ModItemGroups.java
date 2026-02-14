package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup SOUL_SCYTHE_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(SoulScythe.MOD_ID, "soul_scythe_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.LIFE_GEM)).displayName(Text.of("Soul Split"))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.SOUL_SCYTHE);
                        entries.add(ModItems.LIGHTFORGED_GREATSWORD);
                        entries.add(ModItems.PRISMAPIERCER);
                        entries.add(ModItems.PRISMA_DAGGER);
                        entries.add(ModItems.HAMMER_OF_JUSTICE);
                        entries.add(ModItems.LIFE_GEM);
                        entries.add(ModItems.PRISMARITE_SHARD);
                        entries.add(ModItems.SOUL_SHARD);
                        entries.add(ModItems.PLAYER_SOUL);
                        entries.add(ModItems.HAMMER_BLUEPRINT);
                        entries.add(ModBlocks.REINCARNATION_BLOCK);
                        entries.add(ModBlocks.SOUL_SPAWNER);
                        entries.add(ModBlocks.SOUL_VAULT);
                        entries.add(ModBlocks.LIGHT_FORGE);
                        entries.add(ModBlocks.PRISMA_HEART);
                        entries.add(ModBlocks.ANVIL_OF_JUSTICE);
                    }))
                    .build());
    public static void registerItemGroups() {
        SoulScythe.LOGGER.info("Registering mod item groups for " + SoulScythe.MOD_ID);
    }
}
