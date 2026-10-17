package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.Msg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * MaceSwap — automatically selects a mace from the hotbar when falling
 * from height so the impact damage is maximized, then swaps back on landing.
 */
public class MaceSwap extends Module {
    private final DoubleSetting minFall = new DoubleSetting("Min Fall", 1.5, 0.0, 10.0, 0.5);

    private int lastSlot = -1;
    private boolean swapped = false;

    public MaceSwap() {
        super("Mace Swap", "Auto-hold mace while falling", Category.COMBAT);
        addSettings(minFall);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;

        boolean falling = player.fallDistance > minFall.getValue();
        if (falling && !swapped) {
            for (int i = 0; i < 9; i++) {
                ItemStack s = player.getInventory().getMainStacks().get(i);
                if (!s.isEmpty() && s.getItem() == Items.MACE) {
                    lastSlot = player.getInventory().getSelectedSlot();
                    player.getInventory().setSelectedSlot(i);
                    swapped = true;
                    Msg.info("Mace ready — fall " + String.format("%.1f", player.fallDistance));
                    return;
                }
            }
        } else if (!falling && swapped && player.isOnGround()) {
            if (lastSlot >= 0) player.getInventory().setSelectedSlot(lastSlot);
            swapped = false;
            lastSlot = -1;
        }
    }
}
