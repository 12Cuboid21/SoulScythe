package com.cuboidlabs.soulscythe.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.UUID;

public class SoulStormState extends PersistentState {

    public static final Type<SoulStormState> TYPE =
            new Type<>(
                    SoulStormState::new,
                    SoulStormState::fromNbt,
                    null
            );

    public boolean stormActive = false;
    public float stormIntensity = 0.0f;
    public UUID activatorPlayer;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("StormActive", stormActive);
        nbt.putFloat("StormIntensity", stormIntensity);
        if (activatorPlayer != null) {
            nbt.putUuid("StormActivator", activatorPlayer);
        }
        return nbt;
    }

    // 🔥 THIS is the important part
    public static SoulStormState fromNbt(
            NbtCompound nbt,
            RegistryWrapper.WrapperLookup lookup
    ) {
        SoulStormState state = new SoulStormState();
        state.stormActive = nbt.getBoolean("StormActive");
        state.stormIntensity = nbt.getFloat("StormIntensity");
        state.activatorPlayer = nbt.containsUuid("StormActivator")
                ? nbt.getUuid("StormActivator")
                : null;
        return state;
    }

    public static SoulStormState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                SoulStormState.TYPE,
                "soul_storm_state"
        );
    }
}