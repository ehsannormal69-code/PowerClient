package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * KelpESP — counts kelp plants in a radius and surfaces the direction with
 * the densest kelp via the HUD, for diving to farm kelp fast.
 */
public class KelpESP extends Module {
    private int cooldown = 0;

    public KelpESP() {
        super("Kelp ESP", "Find the kelp-densest region near you", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 40;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;

            BlockPos origin = p.getBlockPos();
            int total = 0;
            int[] dirCounts = new int[4];
            BlockPos denseBasis = null;

            for (int dx = -10; dx <= 10; dx++) {
                for (int dz = -10; dz <= 10; dz++) {
                    for (int dy = -10; dy <= 4; dy++) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        if (w.getBlockState(pos).getBlock() == Blocks.KELP) {
                            total++;
                            if (dx > 0) dirCounts[0]++;
                            else if (dx < 0) dirCounts[1]++;
                            if (dz > 0) dirCounts[2]++;
                            else if (dz < 0) dirCounts[3]++;
                        }
                    }
                }
            }

            String[] names = {"East", "West", "South", "North"};
            int bestDir = 0;
            for (int i = 1; i < 4; i++) if (dirCounts[i] > dirCounts[bestDir]) bestDir = i;
            HudCollected.put("kelp", total > 0
                    ? total + " kelp; densest: " + names[bestDir] + " (" + dirCounts[bestDir] + ")"
                    : "No kelp in 20-block box");
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("kelp", null);
    }
}
