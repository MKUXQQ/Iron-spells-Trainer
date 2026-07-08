package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.menu.PortableInscriptionTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent;

public final class OpenInscriptionTableHandler {
    private OpenInscriptionTableHandler() {
    }

    public static void handle(OpenInscriptionTablePayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> context.getSender().openMenu(new SimpleMenuProvider(
                (containerId, inventory, player) -> new PortableInscriptionTableMenu(containerId, inventory),
                Component.translatable("block.irons_spellbooks.inscription_table")
        )));
        context.setPacketHandled(true);
    }
}
