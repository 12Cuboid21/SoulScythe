package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.util.GreatswordAbilityCooldownHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class LightforgedGreatswordItem extends SwordItem {
    public LightforgedGreatswordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    double radius = 6.0;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) return TypedActionResult.success(user.getStackInHand(hand), true);
        if (GreatswordAbilityCooldownHandler.serverPlayerEntityMap.containsKey(user.getUuid())) {
            int cooldownInt = GreatswordAbilityCooldownHandler.serverPlayerEntityMap.get(user.getUuid());
            user.sendMessage(Text.of("The ability is on cooldown: " + cooldownInt), false);
            return TypedActionResult.success(user.getStackInHand(hand), true);
        }

        HitResult raycastEnd = user.raycast(100, 0.0F, false);
        BlockPos hitPos = new BlockPos((int) raycastEnd.getPos().x, (int) raycastEnd.getPos().y, (int) raycastEnd.getPos().z);
        Box box = new Box(
                hitPos.getX() - radius, hitPos.getY() - radius, hitPos.getZ() - radius,
                hitPos.getX() + radius, hitPos.getY() + radius, hitPos.getZ() + radius
        );

        List<Entity> entities = serverWorld.getOtherEntities(user, box);

        if (entities.isEmpty()) {
            user.sendMessage(Text.of("No entities found."), false);
            return TypedActionResult.success(user.getStackInHand(hand));
        }

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntityVictim) {
                livingEntityVictim.addStatusEffect(new StatusEffectInstance(ModEffects.CHAINED_EFFECT,
                        60,
                        0,
                        false,
                        false,
                        false));
            }
            LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
            bolt.setPos(entity.getX(),entity.getY(),entity.getZ());
            serverWorld.spawnEntity(bolt);
        }

        if (user instanceof ServerPlayerEntity serverPlayerUser) GreatswordAbilityCooldownHandler.serverPlayerEntityMap.put(serverPlayerUser.getUuid(), 60);

        return TypedActionResult.success(user.getStackInHand(hand), true);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty().append(Text.literal("Aim and Press ").formatted(Formatting.GOLD)).append(Text.literal("[Right Mouse Button]").formatted(Formatting.GOLD,Formatting.BOLD)).append(Text.literal(" to attack with the ").formatted(Formatting.GOLD)).append(Text.literal("Lightning of Zeus").formatted(Formatting.YELLOW, Formatting.BOLD)));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
