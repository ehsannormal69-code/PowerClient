package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.ModeSetting;
import me.donut.util.Msg;
import net.minecraft.server.integrated.IntegratedServer;

/**
 * QuickMacro — one-tap singleplayer macros (IntegratedServer commands).
 */
public class QuickMacro extends Module {
    private final ModeSetting macro = new ModeSetting("Macro", "Save All", "Heal Me", "Skip Night");

    public QuickMacro() {
        super("Quick Macro", "One-tap bundled singleplayer macros", Category.MISC);
        addSettings(macro);
    }

    @Override
    public void onEnable() {
        run();
        setEnabled(false); // one-shot per toggle
    }

    private void run() {
        IntegratedServer server = me.donut.util.SSP.server();
        if (server == null) {
            Msg.warn("Not in a singleplayer world");
            return;
        }
        Msg.info("Running macro: " + macro.getValue());
        me.donut.util.SSP.onServer(() -> {
            String cmd = switch (macro.getValue()) {
                case "Heal Me" -> "effect give @s minecraft:instant_health 1 1";
                case "Skip Night" -> "time set day";
                default -> "save-all";
            };
            server.getCommandManager().parseAndExecute(
                    server.getCommandSource().withSilent(),
                    cmd
            );
        });
    }
}
