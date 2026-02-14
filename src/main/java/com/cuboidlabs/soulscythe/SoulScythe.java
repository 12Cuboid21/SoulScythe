package com.cuboidlabs.soulscythe;

import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.blockentity.ModBlockEntities;
import com.cuboidlabs.soulscythe.command.SoulSplitCommand;
import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.util.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class SoulScythe implements ModInitializer {
	public static final String MOD_ID = "soulscythe";

	public static final Identifier SOUL_STORM_SYNC =
			Identifier.of("soulscythe", "soul_storm_sync");

	private static boolean shouldReturnPiercer = false;
	private static boolean shouldReturnHammer = false;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final List<RawFilteredPair<Text>> pages = List.of(
			RawFilteredPair.of(Text.of("""
There once was a player DeadRunner he was called.

He would kill others and steal their items.

Once the players found a weird structure in the middle was a weird yellow block,
when they interacted with the block while holding a #######.

It started pointing to a structure,
there they had to fight many monsters and gained #### ######.

Once they got enough it formed a #### ### and when they used it on the yellow block
it gave them a weapon, a weapon they would have only dreamed of,
a weapon so powerful it could split the victims soul and body.

They killed the DeadRunner and decided to never use the weapon again.

One day the weapon disappeared and was never found.

Some people say it was destroyed and some it is still out there,
but no one actually knows.
"""))
	).reversed();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModEffects.registerEffects();
		ModBlockEntities.register();
		ModDataComponentTypes.registerDataComponentTypes();
		SoulScytheNetworking.init();

		WrittenBookContentComponent bookContent =
				new WrittenBookContentComponent(
						RawFilteredPair.of("The Legend Of DeadRunner"), // title
						"Unknown Author",            // author
						0,                                         // generation
						pages,
						false                                      // resolved
				);

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerWorld world : server.getWorlds()) {
				ChainedEffectHandler.tick(world);
				GhostEffectHandler.tick(server);
				GreatswordAbilityCooldownHandler.tick();
				PrismapiercerAbilityCooldownHandler.tick();
				HammerOfJusticeCooldownHandler.tick();
				weaponLock(world);
			}
			SoulStorm.tick(server);
		});

		CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
			commandDispatcher.register(CommandManager.literal("soulsplit").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.literal("switchCraftedScythe").executes(SoulSplitCommand::switchCraftedScythe)).then(CommandManager.literal("switchCraftedSword").executes(SoulSplitCommand::switchCraftedSword)).then(CommandManager.literal("switchCraftedHammer").executes(SoulSplitCommand::switchCraftedHammer)).then(CommandManager.literal("switchCraftedPiercer").executes(SoulSplitCommand::switchCraftedPiercer)));
			/*if (registrationEnvironment.dedicated) {
				commandDispatcher.register(CommandManager.literal("soulsplit").requires(source -> source.hasPermissionLevel(4))/*.then(CommandManager.literal("switchCraftedScythe").executes(SoulSplitCommand::switchCraftedScythe)).then(CommandManager.literal("switchCraftedSword").executes(SoulSplitCommand::switchCraftedSword)).then(CommandManager.literal("switchCraftedHammer").executes(SoulSplitCommand::switchCraftedHammer)).then(CommandManager.literal("switchCraftedSword").executes(SoulSplitCommand::switchCraftedPiercer))*//*);
			}*/
		}));

		ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> {
			ServerWorld world = handler.player.getServerWorld();
			SoulScythe.sync(world);
		});

		LOGGER.info("Hello Fabric world!");
	}

	public static void sync(ServerWorld world) {
		SoulStormState state = SoulStormState.get(world);

		for (ServerPlayerEntity player : world.getPlayers()) {
			//System.out.println("got here");
			//TODO: gotta fix uuid of activatorPlayer being null after struct update
			if (state.activatorPlayer == null) {
				state.activatorPlayer = UUID.randomUUID();
				state.markDirty();
			}
			ServerPlayNetworking.send(player, new SoulStormPayload(state.stormActive, state.stormIntensity, state.activatorPlayer));
		}
	}

	private static void weaponLock(ServerWorld serverWorld) {
		PersistentModData state = PersistentModData.get(serverWorld);
		List<ServerPlayerEntity> players = serverWorld.getPlayers();
		for (ServerPlayerEntity player : players) {
			if (state.piercerOwner != player.getUuid()) {
				if (player.getInventory().contains(new ItemStack(ModItems.PRISMAPIERCER))) {
					int removeSlot = player.getInventory().getSlotWithStack(new ItemStack(ModItems.PRISMAPIERCER));
					player.getInventory().removeStack(removeSlot);
					player.sendMessage(Text.empty().append("You are not worthy this weapon.").formatted(Formatting.DARK_RED));
					shouldReturnPiercer = true;
				}
			}
			if (state.hammerOwner != player.getUuid()) {
				if (player.getInventory().contains(new ItemStack(ModItems.HAMMER_OF_JUSTICE))) {
					int removeSlot = player.getInventory().getSlotWithStack(new ItemStack(ModItems.HAMMER_OF_JUSTICE));
					player.getInventory().removeStack(removeSlot);
					player.sendMessage(Text.empty().append("You are not worthy this weapon.").formatted(Formatting.DARK_RED));
					shouldReturnHammer = true;
				}
			}
		}
		if (shouldReturnPiercer) {
			PlayerEntity piercerOwnerEntity = serverWorld.getPlayerByUuid(state.piercerOwner);
			if (piercerOwnerEntity == null) return;
			piercerOwnerEntity.sendMessage(Text.empty().append("Your piercer has returned.").formatted(Formatting.GOLD));
			piercerOwnerEntity.getInventory().insertStack(new ItemStack(ModItems.PRISMAPIERCER));
			shouldReturnPiercer = false;
		}
		if (shouldReturnHammer) {
			PlayerEntity hammerOwnerEntity = serverWorld.getPlayerByUuid(state.hammerOwner);
			if (hammerOwnerEntity == null) return;
			hammerOwnerEntity.sendMessage(Text.empty().append("Your hammer has returned.").formatted(Formatting.GOLD));
			hammerOwnerEntity.getInventory().insertStack(new ItemStack(ModItems.HAMMER_OF_JUSTICE));
			shouldReturnHammer = false;
		}
	}
}