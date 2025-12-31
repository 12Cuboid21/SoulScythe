package com.cuboidlabs.soulscythe;

import com.cuboidlabs.soulscythe.block.ModBlocks;
import com.cuboidlabs.soulscythe.component.ModDataComponentTypes;
import com.cuboidlabs.soulscythe.effect.ModEffects;
import com.cuboidlabs.soulscythe.item.ModItems;
import com.cuboidlabs.soulscythe.effect.ChainedEffect;
import com.cuboidlabs.soulscythe.util.ChainedEffectHandler;
import com.cuboidlabs.soulscythe.util.GhostEffectHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoulScythe implements ModInitializer {
	public static final String MOD_ID = "soulscythe";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModEffects.registerEffects();
		ModDataComponentTypes.registerDataComponentTypes();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerWorld world : server.getWorlds()) {
				ChainedEffectHandler.tick(world);
				GhostEffectHandler.tick(server);
			}
		});

		LOGGER.info("Hello Fabric world!");
	}
}