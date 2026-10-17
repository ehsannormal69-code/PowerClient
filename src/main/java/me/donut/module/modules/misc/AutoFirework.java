package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Auto Firework — while elytra flying, when your speed decays below the
 * threshold, right-clicks with a firework rocket from the hotbar.
 */
public class AutoFirework extends Module {
    private final DoubleSetting minSpeed = new DoubleSetting("Min Speed", 9.0, 2.0, 30.0, 0.5);
    private int cd = 0;

    public AutoFirework() {
        super("Auto Firework", "Boost elytra with fireworks automatically", Category.MISC);
        addSettings(minSpeed);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null || !player.isGliding() && !player.isGliding()) return;

        Vec3d v = player.getVelocity();
        double speed = Math.sqrt(v.x * v.x + v.z * v.z);
        if (speed >= minSpeed.getValue()) return;

        // offhand rocket preferred
        if (player.getOffHandStack().getItem() == Items.FIREWORK_ROCKET) {
            mc().interactionManager.interactItem(player, Hand.OFF_HAND);
            player.swingHand(Hand.OFF_HAND, true);
            cd = 15;
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getMainStacks().get(i).getItem() == Items.FIREWORK_ROCKET) {
                player.getInventory().setSelectedSlot(i);
                mc().interactionManager.interactItem(player, Hand.MAIN_HAND);
                player.swingHand(Hand.MAIN_HAND, true);
                cd = 15;
                return;
            }
        }
    }
}
