package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * AutoTotem — like AutoInvTotem but also works without any screen open by
 * directly clicking the player screen handler (valid in singleplayer with cheats).
 */
public class AutoTotem extends Module {
    private final DoubleSetting healthThreshold = new DoubleSetting("Health >= ", 10.0, 1.0, 20.0, 0.5);
    private int cd = 0;

    public AutoTotem() {
        super("Auto Totem", "Keeps a totem in offhand below set health", Category.CRYSTAL);
        addSettings(healthThreshold);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null || player.currentScreenHandler == null) return;
        if (player.getHealth() > healthThreshold.getValue()) return;
        if (!player.getOffHandStack().isEmpty()) return;

        // find totem anywhere in main inventory or hotbar
        for (int i = 9; i < player.getInventory().getMainStacks().size(); i++) {
            var s = player.getInventory().getMainStacks().get(i);
            if (!s.isEmpty() && s.getItem() == Items.TOTEM_OF_UNDYING) {
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, player);
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, player);
                cd = 4;
                return;
            }
        }
    }
}
