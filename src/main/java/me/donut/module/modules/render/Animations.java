package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.ModeSetting;

/**
 * Animations — swaps the item swing animation style between vanilla 1.21.x
 * and the snappier 1.7/1.8 style (client-side only).
 */
public class Animations extends Module {
    private final ModeSetting style = new ModeSetting("Style", "1.8 Smooth", "Vanilla", "Fake Swing");

    public Animations() {
        super("Animations", "Change item swing animation style", Category.RENDER);
        addSettings(style);
    }

    public static String currentStyle() {
        var m = me.donut.module.ModuleManager.getByName("Animations");
        if (m == null || !m.isEnabled()) return "Vanilla";
        return ((Animations) m).style.getValue();
    }
}
