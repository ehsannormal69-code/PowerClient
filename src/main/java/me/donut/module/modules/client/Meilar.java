package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.module.ModuleManager;
import me.donut.util.Msg;

import java.util.List;

/**
 * Meilar — the "main preset" module (like a meta-config in real clients):
 * enabling it turns on a curated set of crystal-pvp utility modules,
 * disabling turns the whole preset off again.
 */
public class Meilar extends Module {
    private static final List<String> PRESET = List.of(
            "Auto Totem", "Auto Sprint", "Auto Crystal", "Hitbox", "Aim Assist",
            "Auto Eat", "Auto Firework", "Swing Speed"
    );

    public Meilar() {
        super("Meilar", "Toggle the curated crystal-pvp preset", Category.CLIENT);
        this.setBind(org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    protected void onEnable() {
        int on = 0;
        for (String name : PRESET) {
            Module m = ModuleManager.getByName(name);
            if (m != null && !(m instanceof Meilar)) {
                m.setEnabled(true);
                on++;
            }
        }
        Msg.info("Preset enabled (" + on + " modules)");
        setEnabled(false); // one-shot: it flips the preset, then turns itself off
    }

    @Override
    protected void onDisable() {
        // clicking it when highlighted in the GUI turns the preset OFF
        for (String name : PRESET) {
            Module m = ModuleManager.getByName(name);
            if (m != null) m.setEnabled(false);
        }
        Msg.info("Preset disabled");
    }
}
