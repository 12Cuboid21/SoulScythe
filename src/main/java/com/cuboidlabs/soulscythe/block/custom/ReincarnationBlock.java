package com.cuboidlabs.soulscythe.block.custom;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.item.custom.LifeGemItem;
import com.cuboidlabs.soulscythe.item.custom.PlayerSoulItem;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import com.cuboidlabs.soulscythe.util.PersistentModData;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.datafixer.fix.ItemCustomNameToComponentFix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;

public class ReincarnationBlock extends Block {
    public ReincarnationBlock(Settings settings) {
        super(settings);
    }
    private Set<BlockPos> bookAquired = new HashSet<>();
    private boolean scytheAlrCreated = false;

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.FAIL;
        System.out.println("past 1");
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof LifeGemItem) {
            if (!(world instanceof ServerWorld serverWorld)) return ActionResult.FAIL;
            PersistentModData modDataState = PersistentModData.get(serverWorld);
            if (modDataState.scytheCrafted) {
                player.sendMessage(Text.literal("The scythe has already been crafted."));
                return ActionResult.success(true);
            }
            stack.decrement(1);
            player.getInventory().insertStack(new ItemStack(ModItems.SOUL_SCYTHE));
            LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT,world);
            bolt.setPos(pos.getX(),pos.getY(),pos.getZ());
            bolt.setCosmetic(true);
            world.spawnEntity(bolt);
            modDataState.scytheCrafted = true;
            modDataState.markDirty();
            return ActionResult.success(true);
        } else if (player.getMainHandStack().isEmpty() && !bookAquired.contains(pos)) {
            List<RawFilteredPair<Text>> pages = List.of(
                    RawFilteredPair.of(Text.literal(
                            "There once was a player, DeadRunner he was called.\n\n" +
                                    "He would kill others and steal their items."
                    )),
                    RawFilteredPair.of(Text.literal(
                            "Once the players found a weird structure, in the middle of it was a weird yellow block,\n" +
                                    "when they interacted with the block while holding a #### ###,"
                    )),
                    RawFilteredPair.of(Text.literal(
                            "it gave them a weapon, a weapon they would have only dreamed of,\n" +
                                    "a weapon so powerful it could split the victims soul and body."
                    )),
                    RawFilteredPair.of(Text.literal(
                            "They killed the DeadRunner and decided to never use the weapon again."
                    )),
                    RawFilteredPair.of(Text.literal(
                            "One day the weapon disappeared and was never found."
                    )),
                    RawFilteredPair.of(Text.literal(
                            "Some people say it was destroyed and some it is still out there,\n" +
                                    "but no one actually knows."
                    ))
            );
            WrittenBookContentComponent bookContent =
                    new WrittenBookContentComponent(
                            RawFilteredPair.of("The Legend Of DeadRunner"), // title
                            "???",            // author
                            0,                                         // generation
                            pages,
                            false                                      // resolved
                    );

            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            book.set(
                    DataComponentTypes.WRITTEN_BOOK_CONTENT,
                    bookContent
            );
            player.getInventory().insertStack(book);
            player.sendMessage(Text.of("You found a seemingly old book"), true);
            bookAquired.add(pos);
            return ActionResult.SUCCESS;
        }
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
        world.getPlayerByUuid(userUuid).addStatusEffect(new StatusEffectInstance(ModEffects.REMERGING_EFFECT, 18000, 0, true, false, false));
        return ActionResult.success(true);
    }
}
