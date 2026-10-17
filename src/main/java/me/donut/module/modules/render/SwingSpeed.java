package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;

/**
 * SwingSpeed — alters how fast your held item swings client-side.
 * 1.0 = vanilla, lower = slower, higher = faster.
 */
public class SwingSpeed extends Module {
    private final DoubleSetting speed = new DoubleSetting("Speed", 2.0, 0.25, 4.0, 0.25);

    public SwingSpeed() {
        super("Swing Speed", "Change your arm swing animation speed", Category.RENDER);
        addSettings(speed);
    }

    public static float multiplier() {
        var m = me.donut.module.ModuleManager.getByName("Swing Speed");
        if (!(m instanceof SwingSpeed s) || !s.isEnabled()) return 1.0f;
        return s.speed.getValue().floatValue();
    }
}
