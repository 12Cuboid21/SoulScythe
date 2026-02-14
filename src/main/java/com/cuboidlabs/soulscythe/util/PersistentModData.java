package com.cuboidlabs.soulscythe.util;

import com.cuboidlabs.soulscythe.SoulScythe;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Supplier;

public class PersistentModData extends PersistentState {
    public boolean scytheCrafted = false;
    public boolean greatswordCrafted = false;
    public boolean piercerCrafted = false;
    public UUID piercerOwner = UUID.randomUUID();
    public boolean hammerCrafted = false;
    public UUID hammerOwner = UUID.randomUUID();

    public static final Type<PersistentModData> TYPE =
            new Type<>(
                    PersistentModData::new,
                    PersistentModData::fromNbt,
                    null
            );

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("ScytheCraftedFlag", scytheCrafted);
        nbt.putBoolean("PiercerCraftedFlag", piercerCrafted);
        nbt.putBoolean("HammerCraftedFlag", hammerCrafted);
        nbt.putBoolean("GreatswordCraftedFlag", greatswordCrafted);
        nbt.putUuid("PiercerOwnerFlag", piercerOwner);
        nbt.putUuid("HammerOwnerFlag", hammerOwner);
        return nbt;
    }

    public static PersistentModData fromNbt(
            NbtCompound nbt,
            RegistryWrapper.WrapperLookup lookup
    ) {
        PersistentModData state = new PersistentModData();
        state.scytheCrafted = nbt.getBoolean("ScytheCraftedFlag");
        state.greatswordCrafted = nbt.getBoolean("GreatswordCraftedFlag");
        state.piercerCrafted = nbt.getBoolean("PiercerCraftedFlag");
        state.piercerOwner = nbt.getUuid("PiercerOwnerFlag");
        state.hammerOwner = nbt.getUuid("HammerOwnerFlag");
        state.hammerCrafted = nbt.getBoolean("HammerCraftedFlag");
        return state;
    }

    public static PersistentModData get(ServerWorld serverWorld) {
        return serverWorld.getPersistentStateManager().getOrCreate(
                PersistentModData.TYPE,
                "persistent_mod_state"
        );
    }
}
