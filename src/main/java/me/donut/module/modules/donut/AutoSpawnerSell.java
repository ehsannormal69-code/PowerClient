package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Auto Spawner Sell — scans nearby spawner block entities (ServerWorld view)
 * and reports their mob type + position so you can price spawner sales.
 */
public class AutoSpawnerSell extends Module {
    private int cd = 0;

    public AutoSpawnerSell() {
        super("Auto Spawner Sell", "Spawner farming value estimator", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 100;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            int r = 32;
            var origin = p.getBlockPos();
            var box = new net.minecraft.util.math.Box(
                    net.minecraft.util.math.Vec3d.ofCenter(origin.add(-r, -r, -r)),
                    net.minecraft.util.math.Vec3d.ofCenter(origin.add(r, r, r)));
            for (var be : SSP.loadedBlockEntities(w, box)) {
                if (be instanceof MobSpawnerBlockEntity spawner) {
                    var logic = spawner.getLogic();
                    net.minecraft.entity.Entity shown = logic.getRenderedEntity(w, spawner.getPos());
                    String mob = shown != null ? shown.getType().getName().getString() : "unknown";
                    BlockPos pos = spawner.getPos();
                    Msg.serverLog("Spawner [" + mob + "] at " + pos.toShortString());
                    HudCollected.put("spawner", "Spawner [" + mob + "] " + pos.toShortString());
                }
            }
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("spawner", null);
    }
}
