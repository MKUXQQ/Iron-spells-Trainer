package com.example.portableinscriptiontable.network;

import com.example.portableinscriptiontable.menu.SpellbookReturnPolicy;
import net.minecraft.resources.ResourceLocation;

public final class OpenInscriptionTablePayloadCheck {
    private OpenInscriptionTablePayloadCheck() {
    }

    public static void main(String[] args) {
        manualSpellbookReturnsToInventory();
        equippedSpellbookWritesBackToEquipmentSlot();
        ResourceLocation id = OpenInscriptionTablePayload.TYPE.id();

        assertEquals("portable_inscription_table", id.getNamespace(), "payload namespace");
        assertEquals("open_inscription_table", id.getPath(), "payload path");
    }

    private static void manualSpellbookReturnsToInventory() {
        assertFalse(
                SpellbookReturnPolicy.shouldWriteBackToEquippedSlot(false),
                "manual spellbook return policy"
        );
    }

    private static void equippedSpellbookWritesBackToEquipmentSlot() {
        assertTrue(
                SpellbookReturnPolicy.shouldWriteBackToEquippedSlot(true),
                "equipped spellbook return policy"
        );
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertFalse(boolean actual, String label) {
        if (actual) {
            throw new AssertionError(label + " expected <false> but was <true>");
        }
    }

    private static void assertTrue(boolean actual, String label) {
        if (!actual) {
            throw new AssertionError(label + " expected <true> but was <false>");
        }
    }
}
