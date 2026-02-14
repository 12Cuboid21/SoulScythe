package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.SoulScythe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_SOUL_TOOL = createTag("incorrect_for_soul_tool");
        public static final TagKey<Block> INCORRECT_FOR_PRISMARITE_TOOL = createTag("incorrect_for_prismarite_tool");
        public static final TagKey<Block> INCORRECT_FOR_WEAK_PRISMARITE_TOOL = createTag("incorrect_for_weak_prismarite_tool");
        public static final TagKey<Block> INCORRECT_FOR_INDESTRUCTIBLE_TOOL = createTag("incorrect_for_indestructible_tool");
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SoulScythe.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(SoulScythe.MOD_ID, name));
        }
    }
}
