package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import me.donut.setting.DoubleSetting;

/**
 * Triggerbot — automatically attacks when the crosshair is over any living entity.
 */
public class Triggerbot extends Module {
    private final DoubleSetting headOnly = new DoubleSetting("Head Only", 0.0, 0.0, 1.0, 0.1);
    private final BooleanSetting ignoreArmor = new BooleanSetting("Ignore Armor Stands", true);
    private int cd = 0;

    public Triggerbot() {
        super("Triggerbot", "Auto-attack when crosshair is on a target", Category.COMBAT);
        addSettings(headOnly, ignoreArmor);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null) return;

        if (mc().crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult ehr) {
            var e = ehr.getEntity();
            if (e instanceof net.minecraft.entity.LivingEntity le && le.isAlive()) {
                if (ignoreArmor.getValue() && e instanceof net.minecraft.entity.decoration.ArmorStandEntity) return;
                mc().interactionManager.attackEntity(player, e);
                player.swingHand(net.minecraft.util.Hand.MAIN_HAND, true);
                cd = 6;
            }
        }
    }
}
