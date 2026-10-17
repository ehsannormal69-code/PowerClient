package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import me.donut.util.Msg;

/**
 * FakeElytra — gliding physics without visible elytra state on the player.
 * Singleplayer-safe: we force the client flag and let the integrated server
 * tick continue normally, so movement works but skin/animation look normal.
 */
public class FakeElytra extends Module {
    private final BooleanSetting autoStart = new BooleanSetting("Auto Start", true);

    public FakeElytra() {
        super("Fake Elytra", "Glide physics, no elytra animation/leather look", Category.DONUT);
        addSettings(autoStart);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (!autoStart.getValue()) return;

        // if airborne+falling and not gliding, engage glide silently
        if (!player.isOnGround() && player.getVelocity().y < -0.1d) {
            if (!player.isGliding() && !player.isGliding()) {
                player.startGliding();
            }
        }
    }
}
