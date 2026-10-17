package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;

/**
 * WeatherNotifier — watches the client world's rain/thunder flags and
 * announces changes in chat.
 */
public class WeatherNotifier extends Module {
    private boolean wasRaining = false;
    private boolean wasThunder = false;

    public WeatherNotifier() {
        super("Weather Notifier", "Announce weather changes in chat", Category.MISC);
    }

    @Override
    public void onTick() {
        var world = mc().world;
        if (world == null) return;
        var props = world.getLevelProperties();
        boolean raining = props.isRaining();
        boolean thunder = props.isThundering();

        if (raining != wasRaining) {
            Msg.info(raining ? "Rain started" : "Rain stopped");
            wasRaining = raining;
        }
        if (thunder != wasThunder) {
            Msg.info(thunder ? "Thunderstorm began" : "Thunderstorm ended");
            wasThunder = thunder;
        }
    }
}
