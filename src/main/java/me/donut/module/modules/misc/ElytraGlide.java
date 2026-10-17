package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.util.math.MathHelper;

/**
 * Elytra Glide — anti-stall assist for elytra: softly keeps the pitch
 * inside an efficient band and prevents "dead-air" losses.
 */
public class ElytraGlide extends Module {
    private final DoubleSetting maxPitch = new DoubleSetting("Max Pitch", 25.0, 0.0, 60.0, 1.0);

    public ElytraGlide() {
        super("Elytra Glide", "Elytra anti-stall and pitch assist", Category.MISC);
        addSettings(maxPitch);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null || !player.isGliding()) return;

        float pitch = player.getPitch();
        float limit = maxPitch.getValue().floatValue();
        if (pitch > limit) {
            // too steep downward, stable glide
            float delta = pitch - limit;
            player.setPitch(pitch - delta * 0.25f);
        } else if (pitch < -limit - 20f) {
            // too steep upward (stalling), nose down slightly
            float delta = (-limit - 20f) - pitch;
            player.setPitch(pitch + delta * 0.15f);
        }
        // prevent stall spin
        /* roll removed in 1.21.x */
    }
}
