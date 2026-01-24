package com.cuboidlabs.soulscythe;

import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.blockentity.ModBlockEntities;
import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.effect.ChainedEffect;
import com.cuboidlabs.soulscythe.util.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SoulScythe implements ModInitializer {
	public static final String MOD_ID = "soulscythe";

	public static final Identifier SOUL_STORM_SYNC =
			Identifier.of("soulscythe", "soul_storm_sync");

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
			}
			SoulStorm.tick(server);
		});

		ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> {
			ServerWorld world = handler.player.getServerWorld();
			SoulScythe.sync(world);
		});

		LOGGER.info("Hello Fabric world!");
	}

	public static void sync(ServerWorld world) {
		SoulStormState state = SoulStormState.get(world);

		for (ServerPlayerEntity player : world.getPlayers()) {
			ServerPlayNetworking.send(player, new SoulStormPayload(state.stormActive, state.stormIntensity, state.activatorPlayer));
		}
	}
}