package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.balance.SpellBalanceStore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSpellBalancePayload {
    public RequestSpellBalancePayload() {
    }

    public RequestSpellBalancePayload(FriendlyByteBuf buf) {
    }

    public void write(FriendlyByteBuf buf) {
    }

    public static void handle(RequestSpellBalancePayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ModNetwork.sendToPlayer(
                context.getSender(),
                new SyncSpellBalancePayload(SpellBalanceStore.snapshot(), true)
        ));
        context.setPacketHandled(true);
    }
}
