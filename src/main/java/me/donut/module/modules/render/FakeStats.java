package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.ModeSetting;
import me.donut.util.HudCollected;
import me.donut.util.SSP;

/**
 * FakeStats — decorative HUD like "server tab lists" for recording/screenshots:
 * show fake ping/TPS or fabricated player list lines.
 */
public class FakeStats extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Ping", "TPS", "Player Count");

    public FakeStats() {
        super("Fake Stats", "Decorative fake ping/TPS HUD", Category.RENDER);
        addSettings(mode);
    }

    @Override
    public void onTick() {
        var mc = mc();
        if (mc.player == null) return;
        int seed = (int) (mc.player.age / 20) % 90;
        String line = switch (mode.getValue()) {
            case "TPS" -> "TPS: 20.0 | MSPT: 49.8ms";
            case "Player Count" -> "Players: " + (3 + (seed % 12)) + "/100 online";
            default -> "Ping: " + (18 + (seed % 40)) + "ms";
        };
        HudCollected.put("fakestats", line);
    }

    @Override
    protected void onDisable() {
        HudCollected.put("fakestats", null);
    }
}
