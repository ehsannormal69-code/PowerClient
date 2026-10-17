package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.entity.LivingEntity;

/**
 * AutoJumpReset — watches nearby attackers' swing animations and jumps just
 * before impact, which triggers the classic jump-reset (reduced knockback).
 */
public class AutoJumpReset extends Module {
    private final DoubleSetting range = new DoubleSetting("Range", 3.5, 1.0, 6.0, 0.5);
    private int jumpCd = 0;

    public AutoJumpReset() {
        super("Auto Jump Reset", "Jump right before hits to soften knockback", Category.COMBAT);
        addSettings(range);
    }

    @Override
    public void onTick() {
        if (jumpCd > 0) { jumpCd--; return; }
        var player = mc().player;
        if (player == null || !player.isOnGround()) return;

        // If an attacker is within melee range and mid-swing with windup finished, jump.
        for (var e : player.getEntityWorld().getOtherEntities(player, player.getBoundingBox().expand(range.getValue()))) {
            if (!(e instanceof LivingEntity le) || !le.isAlive()) continue;
            if (le.handSwingProgress > 0.7f && le.handSwinging) {
                player.jump();
                jumpCd = 10; // vanilla jump reset i-frame window
                return;
            }
        }
    }
}
