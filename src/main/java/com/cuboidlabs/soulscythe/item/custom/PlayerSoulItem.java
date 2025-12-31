package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerSoulItem extends Item {
    public PlayerSoulItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        String storedPlayer = stack.get(ModDataComponentTypes.STORED_PLAYER_NAME);
        if (storedPlayer != null) {
            tooltip.add(Text.of("This soul once belonged to the player called " + storedPlayer));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
