package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.module.ModuleManager;
import me.donut.util.Msg;

/**
 * SelfDestruct — classic "cleaner" module: disables every module of the
 * client, leaves the game session, and (in singleplayer) flags itself so
 * no client features are active. Does not delete anything on disk.
 */
public class SelfDestruct extends Module {
    public static boolean triggered = false;

    public SelfDestruct() {
        super("Self Destruct", "Turn everything off and exit world", Category.CLIENT);
    }

    @Override
    protected void onEnable() {
        triggered = true;
        Msg.warn("Self destruct: disabling all modules and leaving world");
        // disable everything (including self afterwards)
        for (Module m : ModuleManager.getAll()) {
            if (m instanceof SelfDestruct) continue;
            m.setEnabled(false);
        }
        var mc = mc();
        if (mc != null && mc.world != null) {
            // SP-safe: leave via disconnect screen
            mc.disconnectWithSavingScreen();
        }
        setEnabled(false);
        triggered = false;
    }
}
