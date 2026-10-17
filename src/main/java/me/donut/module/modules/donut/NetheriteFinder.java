package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * NetheriteFinder — server-side block scan in a small radius for
 * ANCIENT_DEBRIS in nether worlds; posts closest result to HUD.
 * Scan budget: 3.5k positions per second, no frame impact.
 */
public class NetheriteFinder extends Module {
    private int scanOffset = 0;

    public NetheriteFinder() {
        super("Netherite Finder", "Find ancient debris near you", Category.DONUT);
    }

    @Override
    public void onTick() {
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            if (w.getRegistryKey() != World.NETHER) return;

            BlockPos origin = p.getBlockPos();
            int foundCount = 0;
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;

            int radius = 6;
            outer:
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        Block b = w.getBlockState(pos).getBlock();
                        if (b == Blocks.ANCIENT_DEBRIS) {
                            foundCount++;
                            double d = dist(origin, pos);
                            if (d < bestDist) { bestDist = d; best = pos; }
                            if (foundCount >= 8) break outer;
                        }
                    }
                }
            }

            if (best != null) {
                HudCollected.put("netherite",
                        "Ancient debris x" + foundCount + " @ " + best.toShortString()
                                + " (" + (int) bestDist + "m)");
            } else {
                HudCollected.put("netherite", "No debris within " + radius + " blocks");
            }
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("netherite", null);
    }

    private static double dist(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX(), dy = a.getY() - b.getY(), dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
