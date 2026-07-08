package com.example.portableinscriptiontable.client;

public final class SpellBalanceWidgetVisibility {
    private SpellBalanceWidgetVisibility() {
    }

    public static boolean shouldClearFocus(boolean visibleAfterRefresh) {
        return !visibleAfterRefresh;
    }
}
