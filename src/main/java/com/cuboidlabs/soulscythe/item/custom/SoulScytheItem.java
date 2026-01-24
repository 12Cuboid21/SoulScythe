package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.util.ChainedEffectHandler;
import com.cuboidlabs.soulscythe.util.SoulStorm;
import com.cuboidlabs.soulscythe.util.SoulStormState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class SoulScytheItem extends SwordItem {
    private int ticksHeld = 0;
    public SoulScytheItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!(entity instanceof PlayerEntity targetPlayer)) return ActionResult.FAIL;
        if (ChainedEffectHandler.isPlayerIncluded(targetPlayer)) {
            ChainedEffectHandler.removePlayer(targetPlayer);
            targetPlayer.removeStatusEffect(ModEffects.CHAINED_EFFECT);
            return ActionResult.success(true);
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.sendMessage(Text.of("Hold to charge"), true);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        if (ticksHeld == 0) {
            player.sendMessage(Text.of("Charging: 0"), true);
        } else if (ticksHeld == 20) {
            player.sendMessage(Text.of("Charging: 1"), true);
        } else if (ticksHeld == 40) {
            player.sendMessage(Text.of("Charging: 2"), true);
        } else if (ticksHeld == 60) {
            player.sendMessage(Text.of("Charging: 3"), true);
        } else if (ticksHeld == 80) {
            player.sendMessage(Text.of("Charging: 4"), true);
        } else if (ticksHeld == 100) {
            player.sendMessage(Text.of("Charging: full"), true);
        }
        ticksHeld += 1;
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (ticksHeld >= 100) {
            if (world.isClient()) return;
            if (world instanceof ServerWorld serverWorld) {
                if (!(user instanceof PlayerEntity player)) return;
                if (SoulStorm.cooldown > 0) {
                    player.sendMessage(Text.of("The storm is on a cooldown (" + SoulStorm.cooldown + "s)"));
                    ticksHeld = 0;
                    return;
                }
                SoulStormState state = SoulStormState.get(serverWorld);
                if (!state.stormActive) {
                    for (ServerPlayerEntity onePlayer : serverWorld.getPlayers()) {
                        onePlayer.sendMessage(Text.empty().append("The storm has been unleashed").formatted(Formatting.DARK_RED));
                    }
                }
                state.stormActive = !state.stormActive;
                state.stormIntensity = 1;
                state.activatorPlayer = player.getUuid();
                state.markDirty();

                SoulScythe.sync(serverWorld);
                if (state.stormActive == true) SoulStorm.timeLeft = 600;
                else SoulStorm.timeLeft = 0;
            }
        }
        ticksHeld = 0;
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty().append(Text.literal("Hold ").formatted(Formatting.GOLD)).append(Text.literal("[Right Mouse Button]").formatted(Formatting.GOLD,Formatting.BOLD)).append(Text.literal(" for five seconds to unleash the ").formatted(Formatting.GOLD)).append(Text.literal("Soul Storm").formatted(Formatting.LIGHT_PURPLE,Formatting.OBFUSCATED)));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
