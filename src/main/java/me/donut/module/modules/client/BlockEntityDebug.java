package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * BlockEntityDebug — technical HUD: lists every block entity within a
 * configurable radius, ordered by distance, showing type + position.
 * Useful for debugging builds, redstone, and datapack structures.
 */
public class BlockEntityDebug extends Module {
    private final DoubleSetting radius = new DoubleSetting("Radius", 8.0, 2.0, 24.0, 1.0);
    private int cd = 0;

    public BlockEntityDebug() {
        super("Block Entity Debug", "Nearby block entities diagnostic listing", Category.CLIENT);
        addSettings(radius);
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
            int r = radius.getValue().intValue();

            var box = new net.minecraft.util.math.Box(net.minecraft.util.math.Vec3d.ofCenter(origin.add(-r, -r, -r)), net.minecraft.util.math.Vec3d.ofCenter(origin.add(r, r, r)));
            List<BlockEntity> list = new ArrayList<>();
            list.addAll(SSP.loadedBlockEntities(w, box));
            list.sort(Comparator.comparingInt(b -> b.getPos().getManhattanDistance(origin)));

            StringBuilder sb = new StringBuilder();
            int shown = 0;
            for (var be : list) {
                if (shown++ == 5) { sb.append(" +").append(list.size() - 5).append(" more"); break; }
                sb.append(net.minecraft.registry.Registries.BLOCK_ENTITY_TYPE.getId(be.getType()).getPath())
                  .append("@").append(be.getPos().toShortString()).append(" ");
            }
            HudCollected.put("bedebug", list.isEmpty()
                    ? "No block entities nearby" : list.size() + " BEs: " + sb.toString().trim());
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("bedebug", null);
    }
}
