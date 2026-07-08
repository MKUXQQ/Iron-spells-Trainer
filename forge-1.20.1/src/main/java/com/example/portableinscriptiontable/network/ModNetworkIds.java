package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.PortableInscriptionTable;
import net.minecraft.resources.ResourceLocation;

public final class ModNetworkIds {
    public static final ResourceLocation MAIN_CHANNEL =
            new ResourceLocation(PortableInscriptionTable.MOD_ID, "main");
    public static final ResourceLocation OPEN_INSCRIPTION_TABLE =
            new ResourceLocation(PortableInscriptionTable.MOD_ID, "open_inscription_table");

    private ModNetworkIds() {
    }
}
