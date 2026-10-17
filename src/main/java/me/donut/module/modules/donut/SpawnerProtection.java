package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.Msg;
import me.donut.util.SSP;

/**
 * SpawnerProtection — singleplayer guardian for your existing farms:
 * when enabled, any attempt (left-click) to break a spawner within the
 * radius you choose is refused server-side with a chat warning.
 */
public class SpawnerProtection extends Module {
    private final DoubleSetting radius = new DoubleSetting("Radius", 48.0, 8.0, 128.0, 4.0);

    public SpawnerProtection() {
        super("Spawner Protection", "Refuses breaking spawners within radius", Category.DONUT);
        addSettings(radius);
    }

    /** Called from the attack-block server hook; returns true to cancel. */
    public static boolean shouldProtect(net.minecraft.util.math.BlockPos pos) {
        var m = me.donut.module.ModuleManager.getByName("Spawner Protection");
        if (m == null || !m.isEnabled()) return false;
        return true;
    }

    @Override
    public void onEnable() {
        Msg.info("Spawner protection armed within " + radius.getValue() + " blocks");
    }

    public double radiusValue() {
        return radius.getValue();
    }
}
