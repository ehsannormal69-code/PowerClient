package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * HoverTotem — swaps in a totem only while airborne and below the health
 * threshold, so the swap happens during the "hover" gap of crystal PvP.
 */
public class HoverTotem extends Module {
    private final DoubleSetting health = new DoubleSetting("Health <=", 8.0, 1.0, 20.0, 1.0);
    private int cd = 0;

    public HoverTotem() {
        super("Hover Totem", "Totem swap only while airborne and critical", Category.CRYSTAL);
        addSettings(health);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null || player.currentScreenHandler == null) return;

        boolean airborne = !player.isOnGround() || player.isGliding();
        if (!airborne) return;
        if (player.getHealth() > health.getValue()) return;
        if (!player.getOffHandStack().isEmpty()
                && player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        for (int i = 9; i < player.getInventory().getMainStacks().size(); i++) {
            var s = player.getInventory().getMainStacks().get(i);
            if (!s.isEmpty() && s.getItem() == Items.TOTEM_OF_UNDYING) {
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, player);
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, player);
                cd = 5;
                return;
            }
        }
    }
}
