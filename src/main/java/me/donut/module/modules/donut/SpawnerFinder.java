package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * SpawnerFinder — server-side scan for spawners within 24 blocks,
 * reporting each one's mob type. Purely informational.
 */
public class SpawnerFinder extends Module {
    private int cooldown = 0;

    public SpawnerFinder() {
        super("Spawner Finder", "Locate mob spawners around you", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        cooldown = 30;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            BlockPos origin = p.getBlockPos();
            int r = 24;

            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            String bestMob = "";

            var box = new net.minecraft.util.math.Box(
                    net.minecraft.util.math.Vec3d.ofCenter(origin.add(-r, -r, -r)),
                    net.minecraft.util.math.Vec3d.ofCenter(origin.add(r, r, r)));
            for (var be : SSP.loadedBlockEntities(w, box)) {
                if (!(be instanceof MobSpawnerBlockEntity sp)) continue;
                double d = sp.getPos().getManhattanDistance(origin);
                if (d < bestDist) {
                    bestDist = d;
                    best = sp.getPos();
                    net.minecraft.entity.Entity shown = sp.getLogic().getRenderedEntity(w, sp.getPos());
                    bestMob = shown != null ? shown.getType().getName().getString() : "unknown";
                }
            }
            if (best != null) {
                HudCollected.put("spawnerfind",
                        "[" + bestMob + "] spawner " + best.toShortString() + " (" + (int) bestDist + " blocks)");
            } else {
                HudCollected.put("spawnerfind", "No spawners within " + r + " blocks");
            }
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("spawnerfind", null);
    }
}
