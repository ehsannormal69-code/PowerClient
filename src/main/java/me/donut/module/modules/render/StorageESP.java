package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * StorageESP — surfaces every chest/barrel/shulker in a radius on the HUD,
 * sorted by distance. Works through walls because it uses the server view.
 */
public class StorageESP extends Module {
    private final DoubleSetting range = new DoubleSetting("Range", 16.0, 4.0, 48.0, 2.0);
    private int cd = 0;

    public StorageESP() {
        super("Storage ESP", "List nearby containers on the HUD", Category.RENDER);
        addSettings(range);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 10;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            BlockPos origin = p.getBlockPos();
            int r = (int) range.getValue().doubleValue();

            var box = new net.minecraft.util.math.Box(net.minecraft.util.math.Vec3d.ofCenter(origin.add(-r, -r, -r)), net.minecraft.util.math.Vec3d.ofCenter(origin.add(r, r, r)));
            List<LootableContainerBlockEntity> found = new ArrayList<>();
            for (var be : SSP.loadedBlockEntities(w, box)) {
                if (be instanceof LootableContainerBlockEntity l) found.add(l);
            }
            found.sort(Comparator.comparingInt(b -> b.getPos().getManhattanDistance(origin)));

            StringBuilder sb = new StringBuilder();
            int shown = 0;
            for (var l : found) {
                if (shown++ == 6) { sb.append(" +").append(found.size() - 6).append(" more"); break; }
                sb.append(l.getPos().toShortString()).append("  ");
            }
            HudCollected.put("storage", found.isEmpty()
                    ? "No containers nearby" : found.size() + " containers: " + sb);
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("storage", null);
    }
}
