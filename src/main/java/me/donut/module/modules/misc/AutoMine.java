package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

/**
 * Auto Mine — keeps breaking the block you're looking at without holding click.
 */
public class AutoMine extends Module {
    private final BooleanSetting onlyCreative = new BooleanSetting("Creative Only", false);

    public AutoMine() {
        super("Auto Mine", "Hold-break the targeted block automatically", Category.MISC);
        addSettings(onlyCreative);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (onlyCreative.getValue() && !player.isCreative()) return;
        if (mc().currentScreen != null) return;
        if (!(mc().crosshairTarget instanceof BlockHitResult bhr)) return;
        if (bhr.getType() != HitResult.Type.BLOCK) return;

        mc().interactionManager.updateBlockBreakingProgress(bhr.getBlockPos(), bhr.getSide());
        player.swingHand(net.minecraft.util.Hand.MAIN_HAND, false);
    }
}
