package me.donut.module;

import me.donut.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int bind = GLFW.GLFW_KEY_UNKNOWN;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Setting<?>[] getSettings() {
        return settings.toArray(new Setting<?>[0]);
    }

    protected void addSettings(Setting<?>... toAdd) {
        settings.addAll(Arrays.asList(toAdd));
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public String getBindName() {
        if (bind == GLFW.GLFW_KEY_UNKNOWN) {
            return "none";
        }
        String n = GLFW.glfwGetKeyName(bind, 0);
        if (n != null && !n.isEmpty()) {
            return n.toUpperCase();
        }
        // Common special keys
        return switch (bind) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> "`";
            default -> "KEY" + bind;
        };
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean e) {
        if (enabled == e) return;
        enabled = e;
        if (e) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /** Called every client tick while this module is enabled. */
    public void onTick() {
    }

    /** Called when the module is enabled. */
    protected void onEnable() {
    }

    /** Called when the module is disabled. */
    protected void onDisable() {
    }

    protected static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }
}
