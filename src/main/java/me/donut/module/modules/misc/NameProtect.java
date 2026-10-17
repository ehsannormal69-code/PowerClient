package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;

/**
 * NameProtect — hides your real username from chat, tab overlays and sign-in
 * prompts client-side, substituting a placeholder.
 */
public class NameProtect extends Module {
    private final BooleanSetting protectOthers = new BooleanSetting("Protect Friends Too", false);

    public NameProtect() {
        super("Name Protect", "Hides your real username client-side", Category.MISC);
        addSettings(protectOthers);
    }

    public static String protect(String input) {
        var m = me.donut.module.ModuleManager.getByName("Name Protect");
        if (m == null || !m.isEnabled()) return input;
        var mc = me.donut.util.SSP.mc();
        if (mc == null || mc.player == null) return input;
        String real = mc.getSession().getUsername();
        if (real == null || real.isEmpty()) return input;
        return input.replace(real, "Player");
    }
}
