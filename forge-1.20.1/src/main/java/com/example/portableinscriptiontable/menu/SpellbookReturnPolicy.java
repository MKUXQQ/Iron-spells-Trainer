package com.example.portableinscriptiontable.menu;

public final class SpellbookReturnPolicy {
    private SpellbookReturnPolicy() {
    }

    public static boolean shouldWriteBackToEquippedSlot(boolean openedWithEquippedSpellbook) {
        return openedWithEquippedSpellbook;
    }
}
