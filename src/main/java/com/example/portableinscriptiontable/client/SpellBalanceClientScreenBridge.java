package com.example.portableinscriptiontable.client;

import com.example.portableinscriptiontable.balance.SpellBalanceRow;
import com.example.portableinscriptiontable.balance.SpellBalanceStore;
import net.minecraft.client.Minecraft;

import java.util.List;

public final class SpellBalanceClientScreenBridge {
    private SpellBalanceClientScreenBridge() {
    }

    public static void handleSync(List<SpellBalanceRow> rows, boolean openScreen) {
        SpellBalanceStore.applyRowsWithoutSaving(rows);
        Minecraft minecraft = Minecraft.getInstance();
        if (openScreen) {
            minecraft.setScreen(new SpellBalanceScreen(rows));
        } else if (minecraft.screen instanceof SpellBalanceScreen screen) {
            screen.replaceRows(rows);
        }
    }
}
