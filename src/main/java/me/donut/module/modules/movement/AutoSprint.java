package me.donut.module.modules.movement;

import me.donut.module.Category;
import me.donut.module.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("Auto Sprint", "Always sprint while moving forward", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (player.input != null && player.input.hasForwardMovement()) {
            player.setSprinting(true);
        }
    }
}
