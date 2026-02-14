package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.util.GreatswordAbilityCooldownHandler;
import com.cuboidlabs.soulscythe.util.PrismapiercerAbilityCooldownHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.List;

public class PrismaPiercerItem extends SwordItem {
    public PrismaPiercerItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    double radius = 6.0;
    int charge = 0;
    int lastTick = 10;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.getOffHandStack().getItem() instanceof PrismariteShardItem) {
            user.getOffHandStack().decrement(1);
            user.equipStack(EquipmentSlot.OFFHAND, new ItemStack(ModItems.PRISMA_DAGGER));
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        user.sendMessage(Text.of("Hold to charge"), true);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity lUser, ItemStack stack, int remainingUseTicks) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (!(lUser instanceof PlayerEntity user)) return;
        if (lastTick >= 10) {
            lastTick = 0;
            if (charge < 5) charge++;
            user.sendMessage(Text.of("Charge: " + charge), true);
        } else lastTick++;
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity lUser, int remainingUseTicks) {
        if (!(lUser instanceof PlayerEntity user)) return;
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (charge < 5) {
            charge = 0;
            lastTick = 10;
            return;
        }
        if (PrismapiercerAbilityCooldownHandler.serverPlayerEntityMap.containsKey(user.getUuid())) {
            int cooldownInt = PrismapiercerAbilityCooldownHandler.serverPlayerEntityMap.get(user.getUuid());
            user.sendMessage(Text.of("The ability is on cooldown: " + cooldownInt), false);
            charge = 0;
            lastTick = 10;
            return;
        }

        charge = 0;
        lastTick = 10;

        HitResult raycastEnd = user.raycast(100, 0.0F, false);
        BlockPos hitPos = new BlockPos((int) raycastEnd.getPos().x, (int) raycastEnd.getPos().y, (int) raycastEnd.getPos().z);
        Box box = new Box(
                hitPos.getX() - radius, hitPos.getY() - radius, hitPos.getZ() - radius,
                hitPos.getX() + radius, hitPos.getY() + radius, hitPos.getZ() + radius
        );

        List<Entity> entities = serverWorld.getOtherEntities(user, box);

        if (entities.isEmpty()) {
            user.sendMessage(Text.of("No entities found."), false);
            return;
        }

        for (Entity entity : entities) {
            //if (entity instanceof ServerPlayerEntity serverPlayers)
            if (entity instanceof LivingEntity livingEntityVictim) {
                livingEntityVictim.addStatusEffect(new StatusEffectInstance(ModEffects.CHAINED_EFFECT,
                        100,
                        0,
                        false,
                        false,
                        false));

                livingEntityVictim.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,
                        100,
                        10,
                        false,
                        false,
                        false));
            }
            int tpHeight = serverWorld.getTopY(Heightmap.Type.WORLD_SURFACE, 0, 0);
            if (tpHeight < 0) tpHeight = 70;
            SoulScythe.LOGGER.info("Teleporting to Y: " + tpHeight);
            if (entity instanceof ServerPlayerEntity serverPlayerEntityToTp) serverPlayerEntityToTp.setPos(0, tpHeight, 0);
        }

        int tpHeightTheSecond = serverWorld.getTopY(Heightmap.Type.WORLD_SURFACE, 0, 0);
        LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,serverWorld);
        bolt.setPos(0,tpHeightTheSecond,0);
        serverWorld.spawnEntity(bolt);

        if (user instanceof ServerPlayerEntity serverPlayerUser) PrismapiercerAbilityCooldownHandler.serverPlayerEntityMap.put(serverPlayerUser.getUuid(), 60);

        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty().append(Text.literal("Aim and Press ").formatted(Formatting.GOLD)).append(Text.literal("[Right Mouse Button]").formatted(Formatting.GOLD,Formatting.BOLD)).append(Text.literal(" to teleport inflicted players to spawn.").formatted(Formatting.GOLD)));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
