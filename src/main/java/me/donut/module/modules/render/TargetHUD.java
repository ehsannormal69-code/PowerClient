package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * TargetHUD — shows the last-attacked entity's name, health bar and
 * distance in the top-right of the screen.
 */
public class TargetHUD extends Module {
    private static LivingEntity lastTarget = null;
    private static long lastHitTime = 0;

    public TargetHUD() {
        super("Target HUD", "Show last-attacked target's health", Category.RENDER);
        lastTarget = null;
    }

    /** Called from the attack-event hook in ModuleManager. */
    public static void onAttackEntity(net.minecraft.entity.player.PlayerEntity attacker, net.minecraft.entity.Entity target) {
        var m = me.donut.module.ModuleManager.getByName("Target HUD");
        var mc = me.donut.util.SSP.mc();
        if (m == null || !m.isEnabled() || mc == null || mc.player != attacker) return;
        if (target instanceof LivingEntity le) {
            lastTarget = le;
            lastHitTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onTick() {
        if (lastTarget == null) return;
        var mc = mc();
        if (mc.player == null || mc.world == null) { lastTarget = null; return; }
        // clear if stale (>5s) or dead
        if (System.currentTimeMillis() - lastHitTime > 5000 || !lastTarget.isAlive()) {
            lastTarget = null;
            HudCollected.put("targethud", null);
            return;
        }
        String name = lastTarget instanceof PlayerEntity p
                ? p.getName().getString()
                : lastTarget.getType().getName().getString();
        double dist = mc.player.getEyePos().distanceTo(lastTarget.getEntityPos());

        HudCollected.put("targethud", String.format(
                "Target: %s | %.0f/%.0f HP | %.1fm",
                name, lastTarget.getHealth(), lastTarget.getMaxHealth(), dist));
    }

    public static LivingEntity last() {
        return lastTarget;
    }

    @Override
    protected void onDisable() {
        lastTarget = null;
        HudCollected.put("targethud", null);
    }
}
