package com.example.portableinscriptiontable.balance;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpellProjectileBalanceEvents {
    private static final int CAST_MEMORY_TICKS = 20;
    private static final Map<UUID, RecentCast> RECENT_CASTS = new HashMap<>();

    private static Field speedField;

    private SpellProjectileBalanceEvents() {
    }

    public static void onSpellPreCast(SpellPreCastEvent event) {
        Entity caster = event.getEntity();
        if (!(caster instanceof Player player) || player.isCreative()) {
            return;
        }
        SpellBalanceValues values = SpellBalanceStore.valuesFor(ResourceLocation.parse(event.getSpellId()));
        if (values != null && !values.survivalAllowed()) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("spell.portable_inscription_table.survival_disabled"), true);
        }
    }

    public static void onSpellCast(SpellOnCastEvent event) {
        Entity caster = event.getEntity();
        if (caster != null) {
            RECENT_CASTS.put(caster.getUUID(), new RecentCast(
                    ResourceLocation.parse(event.getSpellId()),
                    caster.level().getGameTime()
            ));
        }
    }

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractMagicProjectile projectile)) {
            return;
        }
        Entity owner = projectile.getOwner();
        if (owner == null) {
            return;
        }
        RecentCast cast = RECENT_CASTS.get(owner.getUUID());
        if (cast == null || event.getLevel().getGameTime() - cast.gameTime > CAST_MEMORY_TICKS) {
            return;
        }
        SpellBalanceValues values = SpellBalanceStore.valuesFor(cast.spellId);
        if (values == null) {
            return;
        }
        applyProjectileSpeed(projectile, values.projectileSpeed());
    }

    private static void applyProjectileSpeed(AbstractMagicProjectile projectile, double speedMultiplier) {
        if (speedMultiplier != 1.0) {
            multiplyNumberField(projectile, speedField(), speedMultiplier);
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(speedMultiplier));
        }
    }

    private static void multiplyNumberField(Object target, Field field, double multiplier) {
        if (field == null) {
            return;
        }
        try {
            Class<?> type = field.getType();
            if (type == int.class) {
                field.setInt(target, Math.max(0, (int) Math.round(field.getInt(target) * multiplier)));
            } else if (type == float.class) {
                field.setFloat(target, (float) Math.max(0.0, field.getFloat(target) * multiplier));
            } else if (type == double.class) {
                field.setDouble(target, Math.max(0.0, field.getDouble(target) * multiplier));
            }
        } catch (Exception ignored) {
        }
    }

    private static Field speedField() {
        if (speedField == null) {
            speedField = field("speed");
        }
        return speedField;
    }

    private static Field field(String name) {
        try {
            Field field = AbstractMagicProjectile.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception ignored) {
            return null;
        }
    }

    private record RecentCast(ResourceLocation spellId, long gameTime) {
    }
}
