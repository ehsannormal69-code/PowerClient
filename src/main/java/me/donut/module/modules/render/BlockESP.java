package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * BlockESP — server-side scan of a box around you, reporting counts and the
 * direction of the closest valuable blocks (diamonds, gold, emeralds,
 * amethyst, mob spawners) so you know where to dig.
 */
public class BlockESP extends Module {
    private final DoubleSetting range = new DoubleSetting("Range", 12.0, 4.0, 24.0, 1.0);
    private int cooldown = 0;

    public BlockESP() {
        super("Block ESP", "Find ores and spawners near you", Category.RENDER);
        addSettings(range);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 10;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;

            BlockPos origin = p.getBlockPos();
            int r = range.getValue().intValue();

            Map<Block, BlockPos> closest = new HashMap<>();
            Map<Block, Integer> counts = new HashMap<>();

            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    for (int dz = -r; dz <= r; dz++) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        Block b = w.getBlockState(pos).getBlock();
                        if (isInteresting(b)) {
                            counts.merge(b, 1, Integer::sum);
                            double d = pos.getSquaredDistance(origin);
                            BlockPos cur = closest.get(b);
                            if (cur == null || d < cur.getSquaredDistance(origin)) {
                                closest.put(b, pos);
                            }
                        }
                    }
                }
            }

            if (counts.isEmpty()) {
                HudCollected.put("blockesp", "No target blocks within " + r);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (var entry : counts.entrySet()) {
                BlockPos pos = closest.get(entry.getKey());
                sb.append(entry.getKey().getName().getString())
                  .append(" x").append(entry.getValue())
                  .append(" @").append(pos.toShortString())
                  .append("  ");
            }
            HudCollected.put("blockesp", sb.toString().trim());
        });
    }

    private boolean isInteresting(Block b) {
        return b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE
                || b == Blocks.GOLD_ORE || b == Blocks.NETHER_GOLD_ORE
                || b == Blocks.EMERALD_ORE || b == Blocks.ANCIENT_DEBRIS
                || b == Blocks.AMETHYST_BLOCK || b == Blocks.SPAWNER
                || b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST;
    }

    @Override
    protected void onDisable() {
        HudCollected.put("blockesp", null);
    }
}
