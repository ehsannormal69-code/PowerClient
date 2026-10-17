package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

/**
 * Safe Anchor — checks the ground around a respawn anchor use for danger
 * (lava, void below fire blocks) before letting the player charge/use anchors.
 */
public class SafeAnchor extends Module {
    private final BooleanSetting checkLava = new BooleanSetting("Check Lava", true);
    private final BooleanSetting checkVoid = new BooleanSetting("Check Void", true);

    private boolean suppressedLastUse = false;

    public SafeAnchor() {
        super("Safe Anchor", "Prevents unsafe respawn anchor explosions", Category.CRYSTAL);
        addSettings(checkLava, checkVoid);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;

        var stack = player.getMainHandStack();
        if (stack.getItem() != Items.RESPAWN_ANCHOR) {
            suppressedLastUse = false;
            return;
        }

        // find the single block we're currently looking at
        var hit = mc().crosshairTarget;
        if (hit instanceof net.minecraft.util.hit.BlockHitResult bhit) {
            BlockPos pos = bhit.getBlockPos();
            var state = player.getEntityWorld().getBlockState(pos);
            if (state.getBlock() instanceof RespawnAnchorBlock && state.get(RespawnAnchorBlock.CHARGES) > 0) {
                if (!isSafeSpot(pos.up())) {
                    // swing to cancel the actual explosion broadcast
                    if (!suppressedLastUse) {
                        Msg.warn("Unsafe anchor spot — move to solid ground first!");
                        suppressedLastUse = true;
                    }
                    player.swingHand(net.minecraft.util.Hand.MAIN_HAND, true);
                    return;
                }
            }
        }
        suppressedLastUse = false;
    }

    private boolean isSafeSpot(BlockPos above) {
        var world = mc().world;
        if (world == null) return false;
        for (var dir : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
            BlockPos p = above.offset(dir);
            if (checkLava.getValue() && world.getFluidState(p).isIn(net.minecraft.registry.tag.FluidTags.LAVA)) {
                return false;
            }
        }
        if (checkVoid.getValue()) {
            for (int dy = -1; dy >= -6; dy--) {
                BlockPos p = above.add(0, dy, 0);
                if (!world.getBlockState(p).isAir()) {
                    return true; // ground found
                }
            }
            return false; // all air, or out of world = void
        }
        return true;
    }
}
