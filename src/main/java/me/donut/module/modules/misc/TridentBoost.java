package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * TridentBoost — when launched with riptide and moving through air,
 * re-uses the trident's riptide if the player has water or is in rain (SP cheats:
 * we allow the boost again while remaining in-flight via direct velocity kick).
 */
public class TridentBoost extends Module {
    private final DoubleSetting boost = new DoubleSetting("Boost", 1.5, 0.5, 3.0, 0.25);
    private int cd = 0;

    public TridentBoost() {
        super("Trident Boost", "Extra riptide kick while flying", Category.MISC);
        addSettings(boost);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null) return;
        if (!player.isGliding() && !player.isGliding()) return;
        // riptide state check
        if (!player.isUsingRiptide()) return;

        ItemStack trident = null;
        if (player.getMainHandStack().getItem() == Items.TRIDENT) trident = player.getMainHandStack();
        if (player.getOffHandStack().getItem() == Items.TRIDENT) trident = player.getOffHandStack();
        if (trident == null) return;

        Vec3d look = player.getRotationVector();
        Vec3d kick = look.multiply(boost.getValue());
        player.setVelocity(player.getVelocity().add(kick));
        cd = 15;
    }
}
