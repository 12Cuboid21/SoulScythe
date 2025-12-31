package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.item.custom.PlayerSoulItem;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ReincarnationBlock extends Block {
    public ReincarnationBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.FAIL;
        System.out.println("past 1");
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof PlayerSoulItem)) return ActionResult.FAIL;
        System.out.println("past 2");
        UUID userUuid = stack.get(ModDataComponentTypes.STORED_PLAYER);
        if (userUuid == null) return ActionResult.FAIL;
        System.out.println("past 3");
        String userName = stack.get(ModDataComponentTypes.STORED_PLAYER_NAME);
        ServerWorld serverWorld = (ServerWorld) world;
        MinecraftServer minecraftServer = serverWorld.getServer();
        GameProfile profile = minecraftServer.getUserCache().getByUuid(userUuid).get();
        if (profile == null) return ActionResult.FAIL;
        System.out.println("past 4");
        //minecraftServer.getPlayerManager().getUserBanList().remove(profile);
        GhostEffectHandler.removePlayerByUuid(userUuid);
        stack.decrement(1);
        for (PlayerEntity currentPlayer : ((ServerWorld) world).getPlayers()) {
            currentPlayer.sendMessage(Text.empty().append(Text.literal("The soul and body of ").formatted(Formatting.GOLD)).append(Text.literal(userName).formatted(Formatting.GOLD,Formatting.BOLD)).append(Text.literal(" have met once again.").formatted(Formatting.GOLD)));
        }
        LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        bolt.setPos(pos.getX(),pos.getY(),pos.getZ());
        bolt.setCosmetic(true);
        world.spawnEntity(bolt);
        Objects.requireNonNull(world.getPlayerByUuid(userUuid)).setPos(pos.getX(),pos.getY(),pos.getZ());
        return ActionResult.success(true);
    }
}
