package com.example.portableinscriptiontable.client;

public final class SpellBalanceSelectionStyle {
    private static final int NORMAL_ROW_BACKGROUND = 0x66000000;
    private static final int SELECTED_ROW_BACKGROUND = 0xAA1F2530;
    private static final int FOCUS_BORDER_COLOR = 0xFFFFD36A;

    private SpellBalanceSelectionStyle() {
    }

    public static int rowBackground(boolean selected) {
        return selected ? SELECTED_ROW_BACKGROUND : NORMAL_ROW_BACKGROUND;
    }

    public static int focusBorderColor() {
        return FOCUS_BORDER_COLOR;
    }
}
