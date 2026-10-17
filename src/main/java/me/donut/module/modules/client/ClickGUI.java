package me.donut.module.modules.client;

import me.donut.gui.ClickGuiScreen;
import me.donut.module.Category;
import me.donut.module.Module;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "Open the ClickGUI", Category.CLIENT);
        this.setBind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    protected void onEnable() {
        // Open the GUI screen
        if (mc() != null) {
            mc().setScreen(new ClickGuiScreen());
        }
        // Immediately disable again so it is a one-shot action
        setEnabled(false);
    }
}
