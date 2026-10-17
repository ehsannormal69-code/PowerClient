package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;

public class Fullbright extends Module {
    private static final double ORIGINAL_GAMMA = 1.0;
    private double savedGamma = ORIGINAL_GAMMA;

    public Fullbright() {
        super("Fullbright", "Full brightness in all light levels", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        var opt = mc() != null && mc().options != null ? mc().options.getGamma() : null;
        if (opt != null) {
            savedGamma = opt.getValue();
            opt.setValue(15.0);
        }
    }

    @Override
    protected void onDisable() {
        var opt = mc() != null && mc().options != null ? mc().options.getGamma() : null;
        if (opt != null) {
            opt.setValue(savedGamma);
        }
    }
}
