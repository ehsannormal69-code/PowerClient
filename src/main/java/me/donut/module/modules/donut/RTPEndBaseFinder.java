package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * RTPEndBaseFinder — End-dimension base hunter: server-side scan for
 * obsidian/lodestone/respawn-anchor signatures on the main island or
 * nearby outer islands (End bases are usually those block types).
 */
public class RTPEndBaseFinder extends Module {
    private int cooldown = 0;
    private int scanYOffset = 0;

    public RTPEndBaseFinder() {
        super("RTP End Base Finder", "Detect End bases near obsidian/lodestone", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 30;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            if (w.getRegistryKey() != World.END) {
                HudCollected.put("endbase", "Not in End dimension");
                return;
            }

            BlockPos origin = p.getBlockPos();
            int r = 16;
            int hits = 0;
            BlockPos best = null;
            double bestD = Double.MAX_VALUE;

            Block[] signatures = { Blocks.OBSIDIAN, Blocks.LODESTONE, Blocks.CHEST, Blocks.CRAFTING_TABLE };

            outer:
            for (int dx = -r; dx <= r; dx += 2) {
                for (int dz = -r; dz <= r; dz += 2) {
                    for (int dy = -6; dy <= 6; dy += 2) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        Block b = w.getBlockState(pos).getBlock();
                        for (Block s : signatures) {
                            // skip natural end pillar obsidian columns (check for a chest/lode signature instead)
                            if (s != Blocks.OBSIDIAN && b == s) {
                                hits++;
                                double d = pos.getSquaredDistance(origin);
                                if (d < bestD) { bestD = d; best = pos; }
                            }
                        }
                        if (hits > 10) break outer;
                    }
                }
            }

            if (best != null) {
                HudCollected.put("endbase",
                        "End base @ " + best.toShortString() + " (" + (int) Math.sqrt(bestD) + "m, " + hits + " hits)");
            } else {
                HudCollected.put("endbase", "No End base signature within " + r + " blocks");
            }
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("endbase", null);
    }
}
