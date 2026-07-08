package com.example.portableinscriptiontable.balance;

public record SpellBalanceValues(
        int castTimeTicks,
        double cooldownSeconds,
        double manaCostMultiplier,
        double powerMultiplier
) {
    public static SpellBalanceValues sanitize(
            int castTimeTicks,
            double cooldownSeconds,
            double manaCostMultiplier,
            double powerMultiplier
    ) {
        return new SpellBalanceValues(
                Math.max(0, castTimeTicks),
                cleanNonNegative(cooldownSeconds),
                cleanNonNegative(manaCostMultiplier),
                cleanNonNegative(powerMultiplier)
        );
    }

    public static int secondsToTicks(double seconds) {
        return Math.max(0, (int) Math.round(cleanNonNegative(seconds) * 20.0));
    }

    public double castTimeSeconds() {
        return castTimeTicks / 20.0;
    }

    private static double cleanNonNegative(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, value);
    }
}
