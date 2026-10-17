package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * FastPlace — places blocks every N ticks (no vanilla 4-tick cooldown)
 * while the use key is held, by calling interactBlock directly.
 */
public class FastPlace extends Module {
    private final DoubleSetting tickDelay = new DoubleSetting("Delay", 0.0, 0.0, 4.0, 1.0);

    private int cd = 0;

    public FastPlace() {
        super("Fast Place", "Blocks are placed with almost no delay", Category.MISC);
        addSettings(tickDelay);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null || mc().currentScreen != null) return;
        if (!mc().options.useKey.isPressed()) return;
        if (!(mc().crosshairTarget instanceof BlockHitResult bhr)
                || mc().crosshairTarget.getType() != HitResult.Type.BLOCK) return;

        mc().interactionManager.interactBlock(player, net.minecraft.util.Hand.MAIN_HAND, bhr);
        player.swingHand(net.minecraft.util.Hand.MAIN_HAND, true);
        cd = tickDelay.getValue().intValue();
    }
}
