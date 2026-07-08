package com.example.portableinscriptiontable.client;

import com.example.portableinscriptiontable.PortableInscriptionTable;
import com.example.portableinscriptiontable.network.ModNetwork;
import com.example.portableinscriptiontable.network.OpenInscriptionTablePayload;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class PortableInscriptionClientEvents {
    private PortableInscriptionClientEvents() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(PortableInscriptionKeyMappings::registerKeyMappings);
        MinecraftForge.EVENT_BUS.addListener(PortableInscriptionClientEvents::onClientTick);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (PortableInscriptionKeyMappings.OPEN_INSCRIPTION_TABLE.consumeClick()) {
                ModNetwork.sendToServer(new OpenInscriptionTablePayload());
            }
        }
    }
}
