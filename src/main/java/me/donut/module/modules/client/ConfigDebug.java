package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.module.ModuleManager;
import me.donut.util.Msg;

/**
 * ConfigDebug — dumps the whole module state (enabled/disabled, keybinds)
 * into chat — used to sanity-check the client config as if it were a
 * config file preview.
 */
public class ConfigDebug extends Module {
    public ConfigDebug() {
        super("Config Debug", "Dump module states into chat", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        Msg.info("=== Donut Client config dump ===");
        for (Category c : Category.values()) {
            Msg.chat("— " + c.displayName.toUpperCase() + " —", net.minecraft.util.Formatting.GOLD);
            for (Module m : ModuleManager.getByCategory(c)) {
                String bind = m.getBind() == org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN
                        ? "" : " [" + m.getBindName() + "]";
                Msg.chat("  " + m.getName() + ": " + (m.isEnabled() ? "ON" : "off") + bind,
                        m.isEnabled() ? net.minecraft.util.Formatting.GREEN : net.minecraft.util.Formatting.GRAY);
            }
        }
        setEnabled(false);
    }
}
