package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.EntityHitResult;

/**
 * AutoHitCrystal — only when you are already looking at a crystal via crosshair,
 * attacks it. Useful on crystal servers with limited hit cooldowns.
 */
public class AutoHitCrystal extends Module {
    private final BooleanSetting creativeInsta = new BooleanSetting("Spawn Mode Only", false);

    public AutoHitCrystal() {
        super("Auto Hit Crystal", "Auto attacks the crystal you're looking at", Category.CRYSTAL);
        addSettings(creativeInsta);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (creativeInsta.getValue() && !player.isCreative()) return;

        if (mc().crosshairTarget instanceof EntityHitResult ehr
                && ehr.getEntity() instanceof EndCrystalEntity) {
            mc().interactionManager.attackEntity(player, ehr.getEntity());
            player.swingHand(net.minecraft.util.Hand.MAIN_HAND, true);
        }
    }
}
