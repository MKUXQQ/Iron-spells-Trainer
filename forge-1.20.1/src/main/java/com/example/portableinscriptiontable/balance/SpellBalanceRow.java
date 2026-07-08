package com.example.portableinscriptiontable.balance;

import net.minecraft.resources.ResourceLocation;

public record SpellBalanceRow(
        ResourceLocation spellId,
        String displayName,
        String source,
        String castType,
        SpellBalanceValues values
) {
}
