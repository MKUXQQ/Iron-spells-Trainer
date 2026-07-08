package com.example.portableinscriptiontable.menu;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class PortableInscriptionTableMenu extends InscriptionTableMenu {
    private final boolean openedWithEquippedSpellbook;

    public PortableInscriptionTableMenu(int containerId, Inventory inventory) {
        super(containerId, inventory, ContainerLevelAccess.NULL);
        this.openedWithEquippedSpellbook = Utils.getPlayerSpellbookStack(inventory.player) != null;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (player instanceof ServerPlayer) {
            clearContainer(player, this.scrollContainer);

            if (SpellbookReturnPolicy.shouldWriteBackToEquippedSlot(openedWithEquippedSpellbook)) {
                Utils.setPlayerSpellbookStack(player, getSpellBookSlot().remove(1));
            } else {
                clearContainer(player, this.spellbookContainer);
            }
        }
    }
}
