package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.block.entity.LootableContainerBlockEntity;

/**
 * AuctionSniper — scans loaded block entities around you and reports
 * containers whose custom name looks like a shop/auction listing.
 */
public class AuctionSniper extends Module {
    private int cd = 0;

    public AuctionSniper() {
        super("Auction Sniper", "Find named auction/shop containers nearby", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 40;
        SSP.onServer(() -> {
            var w = SSP.playerWorld();
            var p = SSP.player();
            if (w == null || p == null) return;
            var origin = p.getBlockPos();
            var box = new net.minecraft.util.math.Box(
                    net.minecraft.util.math.Vec3d.ofCenter(origin.add(-48, -24, -48)),
                    net.minecraft.util.math.Vec3d.ofCenter(origin.add(48, 24, 48)));
            for (var be : SSP.loadedBlockEntities(w, box)) {
                if (be instanceof LootableContainerBlockEntity l) {
                    String name = l.getDisplayName().getString();
                    String lower = name.toLowerCase();
                    if (lower.contains("auction") || lower.contains("sell") || lower.contains("shop")) {
                        Msg.serverLog("Shop container @ " + be.getPos().toShortString() + " -> " + name);
                    }
                }
            }
        });
    }
}
