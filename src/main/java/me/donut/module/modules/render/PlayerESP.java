package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import me.donut.util.HudCollected;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/**
 * PlayerESP — surfaces every other player in the world on the HUD with
 * name + distance, and (optionally) applies vanilla Glowing to make them
 * render outlines through walls in singleplayer.
 */
public class PlayerESP extends Module {
    private final BooleanSetting glow = new BooleanSetting("Glow Effect", true);
    private final BooleanSetting throughWalls = new BooleanSetting("Show Distance", true);
    private int cd = 0;

    public PlayerESP() {
        super("Player ESP", "See all players (SP: glow + HUD list)", Category.RENDER);
        addSettings(glow, throughWalls);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 5;

        var mc = mc();
        if (mc.player == null || mc.world == null) return;

        List<AbstractClientPlayerEntity> players = mc.world.getPlayers().stream()
                .filter(p -> p != mc.player)
                .toList();

        // SP-cheat style glow since we own the integrated server
        if (glow.getValue()) {
            SSP_onServerApply(players);
        }

        StringBuilder sb = new StringBuilder();
        for (AbstractClientPlayerEntity p : players) {
            double d = mc.player.getEyePos().distanceTo(p.getEntityPos());
            if (!throughWalls.getValue() || d < 128) {
                sb.append(p.getName().getString())
                  .append(" (").append((int) d).append("m)  ");
            }
        }
        HudCollected.put("playeresp", players.isEmpty() ? "No other players" : sb.toString().trim());
    }

    private void SSP_onServerApply(List<AbstractClientPlayerEntity> clients) {
        me.donut.util.SSP.onServer(() -> {
            var w = me.donut.util.SSP.playerWorld();
            var sp = me.donut.util.SSP.player();
            if (w == null || sp == null) return;
            for (var e : w.getPlayers()) {
                if (e == sp) continue;
                e.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        StatusEffects.GLOWING, 8, 0,
                        false,
                        false, false));
            }
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("playeresp", null);
        me.donut.util.SSP.onServer(() -> {
            var w = me.donut.util.SSP.playerWorld();
            var sp = me.donut.util.SSP.player();
            if (w == null) return;
            for (var e : w.getPlayers()) {
                if (e == sp) continue;
                e.removeStatusEffect(StatusEffects.GLOWING);
            }
        });
    }
}
