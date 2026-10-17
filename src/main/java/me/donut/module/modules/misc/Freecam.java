package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Freecam — client-only camera detachment with noclip through walls.
 * Movement uses W/A/S/D + Space/Shift, arrows to look; the player body stays.
 */
public class Freecam extends Module {
    public static Freecam INSTANCE;
    private final DoubleSetting speed = new DoubleSetting("Speed", 1.0, 0.1, 5.0, 0.1);

    private double ox, oy, oz;
    private float yaw, pitch;

    public Freecam() {
        super("Freecam", "Free noclip camera; your body stays put", Category.MISC);
        addSettings(speed);
        INSTANCE = this;
    }

    @Override
    protected void onEnable() {
        var player = mc().player;
        if (player == null) return;
        ox = oy = oz = 0;
        yaw = player.getYaw();
        pitch = player.getPitch();
    }

    @Override
    protected void onDisable() {
        ox = oy = oz = 0;
    }

    /** The offset the camera should be shifted by (called from the camera mixin). */
    public Vec3d offset() {
        if (!isEnabled()) return Vec3d.ZERO;
        return new Vec3d(ox, oy, oz);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (mc().currentScreen != null) return;

        var opts = mc().options;
        float s = speed.getValue().floatValue();
        double rad = Math.toRadians(yaw);
        double fwd = 0, strafe = 0;

        if (opts.forwardKey.isPressed()) fwd += 1;
        if (opts.backKey.isPressed()) fwd -= 1;
        if (opts.rightKey.isPressed()) strafe += 1;
        if (opts.leftKey.isPressed()) strafe -= 1;
        if (opts.jumpKey.isPressed()) oy += s;
        if (opts.sneakKey.isPressed()) oy -= s;

        ox += (-Math.sin(rad) * fwd + Math.cos(rad) * strafe) * s;
        oz += (Math.cos(rad) * fwd + Math.sin(rad) * strafe) * s;

        long w = mc().getWindow().getHandle();
        if (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) pitch -= 2.5f;
        if (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) pitch += 2.5f;
        if (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) yaw -= 2.5f;
        if (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) yaw += 2.5f;
        pitch = MathHelper.clamp(pitch, -90f, 90f);
    }
}
