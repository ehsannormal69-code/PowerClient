package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import me.donut.util.SSP;
import net.minecraft.block.entity.MobSpawnerBlockEntity;

/**
 * ChunkFinder — reports block-entity counts (spawners, chests) for the chunk
 * you're standing in, so base hunters can recall where loot was.
 */
public class ChunkFinder extends Module {
    private int cd = 0;

    public ChunkFinder() {
        super("Chunk Finder", "Summarize block entities in your chunk", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 20;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            var chunk = w.getWorldChunk(p.getBlockPos());
            if (chunk == null) return;
            int spawners = 0, chests = 0;
            for (var be : chunk.getBlockEntities().values()) {
                if (be instanceof MobSpawnerBlockEntity) spawners++;
                if (be instanceof net.minecraft.block.entity.LootableContainerBlockEntity) chests++;
            }
            var pos = p.getBlockPos();
            HudCollected.put("chunkfinder",
                    String.format("Chunk [%d,%d]: %d spawners, %d containers",
                            pos.getX() >> 4, pos.getZ() >> 4, spawners, chests));
        });
    }

    @Override
    protected void onDisable() {
        HudCollected.put("chunkfinder", null);
    }
}
