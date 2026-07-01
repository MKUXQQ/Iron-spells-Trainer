package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.menu.PortableInscriptionTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OpenInscriptionTableHandler {
    private OpenInscriptionTableHandler() {
    }

    public static void handle(OpenInscriptionTablePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.player().openMenu(new SimpleMenuProvider(
                (containerId, inventory, player) -> new PortableInscriptionTableMenu(containerId, inventory),
                Component.translatable("block.irons_spellbooks.inscription_table")
        )));
    }
}
