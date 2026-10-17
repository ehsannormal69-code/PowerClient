package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;

/**
 * StaticHitboxes — for SP rendering fidelity: disables the client's
 * entity position lerping, so all entities render exactly where the server
 * ticks them (no rubber-band outlines in crystal PvP replays).
 */
public class StaticHitboxes extends Module {
    public static boolean enabled() {
        var m = me.donut.module.ModuleManager.getByName("Static Hitboxes");
        return m != null && m.isEnabled();
    }

    public StaticHitboxes() {
        super("Static Hitboxes", "No entity position lerping/smothing", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        Msg.info("Static hitboxes active — entities render at exact server positions");
    }
}
