package me.donut.module.modules.movement;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.ModeSetting;

/**
 * Placeholder module; real packet-delay logic belongs in a network mixin.
 */
public class FakeLag extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Static", "Adaptive", "Pulse");

    public FakeLag() {
        super("FakeLag", "Simulate network delay to confuse server-side checks", Category.MOVEMENT);
        addSettings(mode);
    }
}
