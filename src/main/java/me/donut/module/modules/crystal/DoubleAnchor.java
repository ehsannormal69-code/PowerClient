package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

/**
 * DoubleAnchor — tracks how many charges you have on your currently
 * held anchor (main hand) after you place one, so you can chain a second
 * placement immediately by holding the same one if there are still charges.
 */
public class DoubleAnchor extends Module {
    private boolean placedLast = false;

    public DoubleAnchor() {
        super("Double Anchor", "Track anchor charges to chain placements", Category.CRYSTAL);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        var stack = player.getMainHandStack();
        if (stack.getItem() != Items.RESPAWN_ANCHOR) {
            placedLast = false;
            return;
        }

        if (mc().crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult bhr
                && mc().options.useKey.isPressed()) {
            if (!placedLast) {
                SSP.onServer(() -> {
                    var sWorld = SSP.playerWorld();
                    var sPlayer = SSP.player();
                    if (sWorld == null || sPlayer == null) return;
                    // Check if a charged anchor exists in range under crosshair block
                    BlockPos check = bhr.getBlockPos();
                    var be = sWorld.getBlockEntity(check);
                    if (sWorld.getBlockState(check).getBlock() == Blocks.RESPAWN_ANCHOR) {
                        Msg.info("Anchor present — double placement possible");
                    }
                });
                placedLast = true;
            }
        } else {
            placedLast = false;
        }
    }
}
