package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * SUSChunkFinder — looks for "suspicious" chunks in a 3x3 radius around you:
 * chunks whose block-entity counts or spawner counts exceed natural norms
 * (data packed in by map makers or admin-run RP events).
 */
public class SUSChunkFinder extends Module {
    private int cooldown = 0;

    public SUSChunkFinder() {
        super("SUS Chunk Finder", "Detects unnaturally-modified chunks nearby", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 60;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            ChunkPos center = p.getChunkPos();

            String report = null;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    var chunk = w.getChunk(center.x + dx, center.z + dz);
                    int spawners = 0, containers = 0;
                    for (var be : chunk.getBlockEntities().values()) {
                        if (be instanceof net.minecraft.block.entity.MobSpawnerBlockEntity) spawners++;
                        if (be instanceof net.minecraft.block.entity.LootableContainerBlockEntity) containers++;
                    }
                    // >2 spawners per chunk is unnatural; >8 containers suggests an over-stuffed base
                    if (spawners > 2) {
                        report = "SUS: " + (center.x + dx) + "," + (center.z + dz)
                                + " has " + spawners + " spawners";
                        break;
                    }
                    if (containers > 8) {
                        report = "SUS: " + (center.x + dx) + "," + (center.z + dz)
                                + " has " + containers + " containers";
                        break;
                    }
                }
            }
            HudCollected.put("suschunk", report == null
                    ? "3x3 chunks look natural"
                    : report);
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("suschunk", null);
    }
}
