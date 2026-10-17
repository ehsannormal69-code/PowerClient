package me.donut.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Simple client-side chat feedback helper. */
public class Msg {
    private Msg() {}

    public static void chat(String message, Formatting color) {
        var mc = SSP.mc();
        if (mc == null || mc.player == null) return;
        mc.player.sendMessage(
                Text.literal(message).formatted(color),
                false
        );
    }

    public static void info(String message) {
        chat("[Donut] " + message, Formatting.AQUA);
    }

    public static void warn(String message) {
        chat("[Donut] " + message, Formatting.YELLOW);
    }

    /** Server-side status line printed to the console/log of the integrated server. */
    public static void serverLog(String message) {
        var s = SSP.server();
        if (s != null) {
            s.getCommandSource().sendMessage(Text.literal("[Donut] " + message));
        }
    }
}
