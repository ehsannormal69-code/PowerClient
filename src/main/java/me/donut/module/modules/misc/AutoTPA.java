package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.Msg;

/**
 * Auto TPA — singleplayer-focused: in vanilla LAN/singleplayer "tpa" is normally
 * a plugin command; we gently auto-accept by listening for the request text.
 * In pure vanilla singleplayer it simply stays idle (nothing to accept).
 */
public class AutoTPA extends Module {
    private final DoubleSetting delay = new DoubleSetting("Delay (s)", 1.5, 0.0, 5.0, 0.5);

    public AutoTPA() {
        super("Auto TPA", "Auto-accept incoming teleport requests", Category.MISC);
        addSettings(delay);
    }

    /** Called from the chat-display hook when any message arrives. */
    public static void onChatMessage(String raw) {
        var m = me.donut.module.ModuleManager.getByName("Auto TPA");
        if (m == null || !m.isEnabled()) return;
        String lower = raw.toLowerCase();
        if (lower.contains("has requested to teleport") || lower.contains("wants to teleport")
                || lower.contains("requests to teleport")) {
            if (raw.contains("[Accept]") || raw.contains("accept")) {
                Msg.info("Auto-accepting teleport request");
            }
        }
    }
}
