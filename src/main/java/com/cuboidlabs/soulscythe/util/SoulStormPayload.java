package com.cuboidlabs.soulscythe.util;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record SoulStormPayload(boolean active, float intensity, UUID activatorPlayer)
        implements CustomPayload {

    public static final Id<SoulStormPayload> ID =
            new Id<>(Identifier.of("soulscythe", "soul_storm"));

    public static final PacketCodec<RegistryByteBuf, SoulStormPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOL, SoulStormPayload::active,
                    PacketCodecs.FLOAT, SoulStormPayload::intensity,
                    Uuids.PACKET_CODEC, SoulStormPayload::activatorPlayer,
                    SoulStormPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
