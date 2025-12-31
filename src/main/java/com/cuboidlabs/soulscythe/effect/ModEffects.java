package com.cuboidlabs.soulscythe.effect;

import com.cuboidlabs.soulscythe.SoulScythe;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> CHAINED_EFFECT = registerStatusEffect("chained_effect", new ChainedEffect()/*.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(SoulScythe.MOD_ID,"chained_effect"),0f,EntityAttributeModifier.Operation.ADD_VALUE).addAttributeModifier(EntityAttributes.GENERIC_GRAVITY, Identifier.of(SoulScythe.MOD_ID,"chained_effect"),0f,EntityAttributeModifier.Operation.ADD_VALUE).addAttributeModifier(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, Identifier.of(SoulScythe.MOD_ID,"chained_effect"), 0f,EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)*/);
    public static final RegistryEntry<StatusEffect> GHOST_EFFECT = registerStatusEffect("ghost_effect", new GhostEffect(StatusEffectCategory.HARMFUL,0x545454));

    public static void registerEffects() {
        SoulScythe.LOGGER.info("Registering mod items for: " + SoulScythe.MOD_ID);
    }

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulScythe.MOD_ID,name), statusEffect);
    }
}
