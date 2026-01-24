package com.cuboidlabs.soulscythe.item;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.item.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SOUL_SCYTHE = registerItem("soul_scythe",new SoulScytheItem(ModToolMaterials.SOUL ,new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.SOUL, 1, -2.4f))));
    public static final Item PLAYER_SOUL = registerItem("player_soul",new PlayerSoulItem(new Item.Settings().maxCount(1)));
    public static final Item SHADER_DEBUG_ITEM = registerItem("shader_debug_item", new ShaderDebugItem(new Item.Settings().maxCount(1)));
    public static final Item LIFE_GEM = registerItem("life_gem", new LifeGemItem(new Item.Settings().maxCount(1)));
    public static final Item SOUL_SHARD = registerItem("soul_shard", new SoulShardItem(new Item.Settings().maxCount(3)));

    public static void registerModItems() {
        SoulScythe.LOGGER.info("Registering mod items for: " + SoulScythe.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(SOUL_SCYTHE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(PLAYER_SOUL);
        });
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SoulScythe.MOD_ID, name), item);
    }
}
