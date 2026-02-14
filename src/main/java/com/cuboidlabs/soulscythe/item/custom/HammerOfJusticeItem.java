package com.cuboidlabs.soulscythe.item.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.util.GreatswordAbilityCooldownHandler;
import com.cuboidlabs.soulscythe.util.HammerOfJusticeCooldownHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class HammerOfJusticeItem extends PickaxeItem {
    public HammerOfJusticeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) return TypedActionResult.success(user.getStackInHand(hand), true);
        if (HammerOfJusticeCooldownHandler.serverPlayerEntityMap.containsKey(user.getUuid())) {
            Integer userCooldown = HammerOfJusticeCooldownHandler.serverPlayerEntityMap.getOrDefault(user.getUuid(), -1);
            String cooldownUse = userCooldown.toString();
            user.sendMessage(Text.of("The ability is on cooldown: " + cooldownUse), false);
            return TypedActionResult.success(user.getStackInHand(hand), true);
        }
        BlockPos hitPos = new BlockPos(user.getBlockPos().getX(), user.getBlockPos().getY(), user.getBlockPos().getZ());
        int fireRad = 4;
        Box box = new Box(
                hitPos.getX() - fireRad, hitPos.getY() - fireRad, hitPos.getZ() - fireRad,
                hitPos.getX() + fireRad, hitPos.getY() + fireRad, hitPos.getZ() + fireRad
        );

        List<Entity> entities = serverWorld.getOtherEntities(user, box);

        if (entities.isEmpty()) {
            user.sendMessage(Text.of("No entities found."), false);
            return TypedActionResult.success(user.getStackInHand(hand));
        }

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setOnFireFor(20);
                livingEntity.damage(livingEntity.getDamageSources().playerAttack(user), 6);
            }
        }

        spawnFlashCircle(serverWorld, user);
        if (user instanceof PlayerEntity serverPlayerUser) HammerOfJusticeCooldownHandler.serverPlayerEntityMap.put(serverPlayerUser.getUuid(), 60);
        return TypedActionResult.success(user.getStackInHand(hand), true);
    }

    public static void spawnFlashCircle(ServerWorld world, PlayerEntity player) {
        double radius = 0;
        double maxRadius = 8;
        int points = 32;

        double cx = player.getX();
        double cy = player.getY() + 1.0;
        double cz = player.getZ();

        for (int allcircles = 0; allcircles < maxRadius; allcircles++) {
            radius++;
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i;

                double x = cx + Math.cos(angle) * radius;
                double z = cz + Math.sin(angle) * radius;

                world.spawnParticles(
                        ParticleTypes.LAVA,
                        x, cy, z,
                        2,
                        0, 0, 0,
                        0
                );
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty().append(Text.literal("Press ").formatted(Formatting.GOLD)).append(Text.literal("[Right Mouse Button]").formatted(Formatting.GOLD,Formatting.BOLD)).append(Text.literal(" to set near players on fire.").formatted(Formatting.GOLD)));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
