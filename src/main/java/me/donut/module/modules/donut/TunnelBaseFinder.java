package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * TunnelBaseFinder — scans a slice below the player for torch/lantern spam
 * and for 2x1 air corridors (mined tunnels) — the classic signature of a
 * stripped mine or a hidden underground base.
 */
public class TunnelBaseFinder extends Module {
    private int cooldown = 0;

    public TunnelBaseFinder() {
        super("Tunnel Base Finder", "Detects mined tunnels/underground bases", Category.DONUT);
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
            int torches = 0;
            int airPockets = 0;
            BlockPos torchPos = null;

            for (int dx = -12; dx <= 12; dx += 1) {
                for (int dz = -12; dz <= 12; dz += 1) {
                    for (int dy = -14; dy <= 2; dy += 1) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        Block b = w.getBlockState(pos).getBlock();
                        if (b == Blocks.TORCH || b == Blocks.SOUL_TORCH || b == Blocks.LANTERN) {
                            torches++;
                            if (torchPos == null) torchPos = pos;
                        } else if (b == Blocks.AIR && w.getBlockState(pos.up()).isAir()
                                && w.getBlockState(pos.down()).isOpaqueFullCube()) {
                            airPockets++;
                        }
                    }
                }
            }

            String report;
            if (torches > 5) {
                report = "Tunnel signs: " + torches + " torches"
                        + (torchPos != null ? " first @" + torchPos.toShortString() : "");
            } else if (airPockets > 40) {
                report = "Possible mine: " + airPockets + " cave/tunnel blocks";
            } else {
                report = "No tunnel base signs nearby";
            }
            HudCollected.put("tunnelbase", report);
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("tunnelbase", null);
    }
}
