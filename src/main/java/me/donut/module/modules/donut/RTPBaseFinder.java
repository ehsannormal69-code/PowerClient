package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * RTPBaseFinder — after a random teleport, server-scans the destination chunk
 * for player-placed blocks (chest, furnace, crafting table, torch, basalt paths)
 * that suggest an (abandoned or active) base.
 */
public class RTPBaseFinder extends Module {
    private int cooldown = 0;

    public RTPBaseFinder() {
        super("RTP Base Finder", "Scan current chunk for signs of a base", Category.DONUT);
    }

    private static final Block[] SIGNS = {
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.FURNACE, Blocks.BLAST_FURNACE,
            Blocks.SMOKER, Blocks.CRAFTING_TABLE, Blocks.TORCH, Blocks.SOUL_TORCH,
            Blocks.LANTERN, Blocks.SOUL_LANTERN, Blocks.ANVIL, Blocks.ENCHANTING_TABLE,
            Blocks.BREWING_STAND, Blocks.BEEHIVE, Blocks.OBSIDIAN
    };

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 40; // re-scan every 2s
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            ChunkPos cp = p.getChunkPos();
            var chunk = w.getChunk(cp.x, cp.z);
            var beMap = chunk.getBlockEntities();
            int chests = 0, torches = 0, other = 0;

            // block-entities are cheap proxy for "someone built here"
            for (var be : beMap.values()) {
                var type = be.getType();
                String id = net.minecraft.registry.Registries.BLOCK_ENTITY_TYPE.getId(type).getPath();
                if (id.contains("chest")) chests++;
                else if (id.contains("torch") || id.contains("lantern")) torches++;
                else other++;
            }

            // also scan block-signature counts on a subsample grid
            int sigHits = 0;
            var origin = new BlockPos(cp.getStartX(), 64, cp.getStartZ());
            for (int dx = 0; dx < 16; dx += 2) {
                for (int dz = 0; dz < 16; dz += 2) {
                    for (int dy = -8; dy < 24; dy += 2) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        Block b = w.getBlockState(pos).getBlock();
                        for (Block s : SIGNS) {
                            if (b == s) { sigHits++; break; }
                        }
                    }
                }
            }

            if (chests + torches + other > 0 || sigHits > 3) {
                String report = "Chunk [" + cp.x + "," + cp.z + "] base signs: "
                        + chests + " chests, " + torches + " lights, " + other
                        + " BEs, " + sigHits + " signature hits";
                HudCollected.put("rtpbase", report);
                Msg.serverLog(report);
            }
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("rtpbase", null);
    }
}
