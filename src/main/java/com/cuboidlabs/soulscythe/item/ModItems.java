package com.cuboidlabs.soulscythe.item;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.item.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SOUL_SCYTHE = registerItem("soul_scythe",new SoulScytheItem(ModToolMaterials.SOUL ,new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.SOUL, 1, -2.4f)).maxDamage(0)));
    public static final Item PRISMAPIERCER = registerItem("prismapiercer",new PrismaPiercerItem(ModToolMaterials.PRISMARITE,new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 2, -1.2f)).maxDamage(0)));
    public static final Item PRISMA_DAGGER = registerItem("prisma_dagger",new PrismaDaggerItem(ModToolMaterials.WEAKPRISMARITE,new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 0, -0.2f)).maxDamage(3)));
    public static final Item LIGHTFORGED_GREATSWORD = registerItem("lightforged_greatsword",new LightforgedGreatswordItem(ModToolMaterials.INDESTRUCTIBLE,new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 6, -3.2f)).maxDamage(0))); //TODO: větší attack damage
    public static final Item HAMMER_OF_JUSTICE = registerItem("hammer_of_justice",new HammerOfJusticeItem(ModToolMaterials.INDESTRUCTIBLE,new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 11, -3.9f)).maxDamage(0)));
    public static final Item PLAYER_SOUL = registerItem("player_soul",new PlayerSoulItem(new Item.Settings().maxCount(1)));
    public static final Item SHADER_DEBUG_ITEM = registerItem("shader_debug_item", new ShaderDebugItem(new Item.Settings().maxCount(1)));
    public static final Item LIFE_GEM = registerItem("life_gem", new LifeGemItem(new Item.Settings().maxCount(1)));
    public static final Item PRISMARITE_SHARD = registerItem("prismarite_shard", new PrismariteShardItem(new Item.Settings().maxCount(16)));
    public static final Item SOUL_SHARD = registerItem("soul_shard", new SoulShardItem(new Item.Settings().maxCount(3)));
    public static final Item HAMMER_BLUEPRINT = registerItem("hammer_blueprint", new Item(new Item.Settings().maxCount(1)));

    public static void registerModItems() {
        SoulScythe.LOGGER.info("Registering mod items for: " + SoulScythe.MOD_ID);

        /*ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(SOUL_SCYTHE);
            fabricItemGroupEntries.add(PRISMAPIERCER);
            fabricItemGroupEntries.add(FURY_AXE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(PLAYER_SOUL);
            fabricItemGroupEntries.add(SOUL_SHARD);
            fabricItemGroupEntries.add(LIFE_GEM);
        });*/
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SoulScythe.MOD_ID, name), item);
    }
}
