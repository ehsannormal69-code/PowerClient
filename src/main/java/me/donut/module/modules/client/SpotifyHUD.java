package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SpotifyHUD — reads a small file the user (or any external tool) keeps
 * updated — donutclient/now_playing.txt in the game dir — and mirrors its
 * first line on the HUD like a Spotify overlay. Nothing invasive; works
 * with the official Spotify client, browser, or any "now playing" exporter.
 */
public class SpotifyHUD extends Module {
    private static Path file() {
        var mc = me.donut.util.SSP.mc();
        Path dir = mc.runDirectory.toPath().resolve("donutclient");
        try {
            Files.createDirectories(dir);
        } catch (Exception ignored) {
        }
        return dir.resolve("now_playing.txt");
    }

    public SpotifyHUD() {
        super("Spotify HUD", "Show your 'now playing' text on the HUD", Category.CLIENT);
    }

    @Override
    public void onTick() {
        try {
            if (Files.exists(file())) {
                String line = Files.readAllLines(file()).stream().findFirst().orElse("");
                HudCollected.put("spotify", line.isEmpty() ? null : "\u266B " + line);
            }
        } catch (Exception e) {
            HudCollected.put("spotify", null);
        }
    }

    @Override
    protected void onDisable() {
        HudCollected.put("spotify", null);
    }
}
