package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * Auto Tool — equips the best tool in the hotbar for the block you mine.
 */
public class AutoTool extends Module {
    private final BooleanSetting hotbarOnly = new BooleanSetting("Hotbar Only", true);

    public AutoTool() {
        super("Auto Tool", "Best tool for the job, automatically", Category.MISC);
        addSettings(hotbarOnly);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null || mc().currentScreen != null) return;
        if (!(mc().crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult bhr)) return;

        // only engage while actually mining (client attack key)
        if (!mc().options.attackKey.isPressed()) return;

        BlockState state = player.getEntityWorld().getBlockState(bhr.getBlockPos());
        if (state.isAir()) return;

        int bestSlot = -1;
        float bestSpeed = player.getMainHandStack().getMiningSpeedMultiplier(state);

        int end = hotbarOnly.getValue() ? 9 : player.getInventory().getMainStacks().size();
        for (int i = 0; i < end; i++) {
            ItemStack s = player.getInventory().getMainStacks().get(i);
            if (s.isEmpty() || !s.isSuitableFor(state)) continue;
            float speed = s.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed + 0.01f) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        if (bestSlot >= 0 && bestSlot != player.getInventory().getSelectedSlot()) {
            player.getInventory().setSelectedSlot(bestSlot);
        }
    }
}
