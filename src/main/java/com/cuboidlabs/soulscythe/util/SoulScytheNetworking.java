package com.cuboidlabs.soulscythe.util;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class SoulScytheNetworking {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(
                SoulStormPayload.ID,
                SoulStormPayload.CODEC
        );
    }
}
