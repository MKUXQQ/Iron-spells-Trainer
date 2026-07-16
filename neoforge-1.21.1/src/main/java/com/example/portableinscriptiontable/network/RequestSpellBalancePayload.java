package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.PortableInscriptionTable;
import com.example.portableinscriptiontable.balance.SpellBalanceStore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestSpellBalancePayload() implements CustomPacketPayload {
    public static final Type<RequestSpellBalancePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PortableInscriptionTable.MOD_ID, "request_spell_balance")
    );
    public static final StreamCodec<ByteBuf, RequestSpellBalancePayload> STREAM_CODEC =
            StreamCodec.unit(new RequestSpellBalancePayload());

    public static void handle(RequestSpellBalancePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> PacketDistributor.sendToPlayer(
                (net.minecraft.server.level.ServerPlayer) context.player(),
                new SyncSpellBalancePayload(
                        SpellBalanceStore.snapshot(),
                        true
                )
        ));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
