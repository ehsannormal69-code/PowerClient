package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.Msg;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Anchor Macro — looks at the block in front, and when holding an anchor
 * places it + charges it with glowstone in one tick (classic 1.12 "anchor macro").
 */
public class AnchorMacro extends Module {
    private final DoubleSetting delay = new DoubleSetting("Delay", 2.0, 0.0, 10.0, 1.0);

    private int cooldown = 0;

    public AnchorMacro() {
        super("Anchor Macro", "Places and charges respawn anchors fast", Category.CRYSTAL);
        addSettings(delay);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null || cooldown > 0) {
            if (cooldown > 0) cooldown--;
            return;
        }

        if (player.getOffHandStack().getItem() == Items.GLOWSTONE
                && player.getMainHandStack().getItem() == Items.RESPAWN_ANCHOR) {
            HitResult hit = mc().crosshairTarget;
            if (hit instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = bhr.getBlockPos().offset(bhr.getBlockPos() != null ? bhr.getSide() : Direction.UP);
                // place anchor
                mc().interactionManager.interactBlock(player, Hand.MAIN_HAND,
                        new BlockHitResult(Vec3d.ofCenter(pos), bhr.getSide(), bhr.getBlockPos(), false));
                player.swingHand(Hand.MAIN_HAND, true);
                cooldown = delay.getValue().intValue();
                Msg.info("Anchor placed & charging cycle started");
            }
        }
    }
}
