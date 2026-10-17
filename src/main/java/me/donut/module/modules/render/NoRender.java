package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;

/**
 * NoRender — disables selected render effects client-side to reduce noise.
 */
public class NoRender extends Module {
    public final BooleanSetting rain = new BooleanSetting("No Rain", true);
    public final BooleanSetting portalOverlay = new BooleanSetting("No Portal Overlay", false);
    public final BooleanSetting vignette = new BooleanSetting("No Vignette", false);

    public NoRender() {
        super("NoRender", "Disable rain, portal overlay and vignette rendering", Category.RENDER);
        addSettings(rain, portalOverlay, vignette);
    }

    public static boolean hideRain() {
        var m = me.donut.module.ModuleManager.getByName("NoRender");
        if (!(m instanceof NoRender nr) || !nr.isEnabled()) return false;
        return nr.rain.getValue();
    }

    public static boolean hidePortal() {
        var m = me.donut.module.ModuleManager.getByName("NoRender");
        if (!(m instanceof NoRender nr) || !nr.isEnabled()) return false;
        return nr.portalOverlay.getValue();
    }

    public static boolean hideVignette() {
        var m = me.donut.module.ModuleManager.getByName("NoRender");
        if (!(m instanceof NoRender nr) || !nr.isEnabled()) return false;
        return nr.vignette.getValue();
    }
}
