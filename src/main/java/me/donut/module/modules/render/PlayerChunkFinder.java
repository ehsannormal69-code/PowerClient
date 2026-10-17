package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * PlayerChunkFinder — scans a 5x5 chunk radius server-side and reports which
 * chunks contain player-placed indicators (chests, beds, doors, campfires,
 * jukeboxes, note blocks), so you can orient toward bases/settlements.
 */
public class PlayerChunkFinder extends Module {
    private int cooldown = 0;

    public PlayerChunkFinder() {
        super("Player Chunk Finder", "Find chunks with signs of players", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 100;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            ChunkPos center = p.getChunkPos();

            StringBuilder sb = new StringBuilder();
            int hot = 0;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    var chunk = w.getChunk(center.x + dx, center.z + dz);
                    int score = 0;
                    for (var be : chunk.getBlockEntities().values()) {
                        var id = net.minecraft.registry.Registries.BLOCK_ENTITY_TYPE
                                .getId(be.getType()).getPath();
                        if (id.contains("chest") || id.contains("barrel")
                                || id.contains("bed") || id.contains("door")
                                || id.contains("jukebox") || id.contains("noteblock")
                                || id.contains("campfire")) {
                            score++;
                        }
                    }
                    if (score > 0) {
                        hot++;
                        sb.append("[").append(center.x + dx).append(",").append(center.z + dz)
                          .append("]:").append(score).append(" ");
                    }
                }
            }
            HudCollected.put("playerchunk", hot == 0
                    ? "No player-built chunks within 5x5 radius"
                    : hot + " built chunks: " + sb.toString().trim());
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("playerchunk", null);
    }
}
