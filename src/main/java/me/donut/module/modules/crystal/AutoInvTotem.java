package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

/**
 * AutoInvTotem — pops a totem of undying from inventory to the offhand slot
 * whenever the offhand goes empty (classic crystal-pvp auto totem).
 */
public class AutoInvTotem extends Module {
    private int cooldown = 0;

    public AutoInvTotem() {
        super("Auto Inv Totem", "Refills offhand with totems automatically", Category.CRYSTAL);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        var player = mc().player;
        if (player == null) return;
        if (!player.getOffHandStack().isEmpty()) return;
        if (player.currentScreenHandler == null) return;
        // Only run when a screen allowing slot dragging is open OR nothing is (classic limitation)
        if (mc().currentScreen != null && !(mc().currentScreen instanceof InventoryScreen)) return;

        // find a totem in main inventory
        for (int i = 0; i < player.getInventory().getMainStacks().size(); i++) {
            var s = player.getInventory().getMainStacks().get(i);
            if (!s.isEmpty() && s.getItem() == Items.TOTEM_OF_UNDYING) {
                int slot = net.minecraft.screen.PlayerScreenHandler.INVENTORY_START + i - 9 >= 0
                        ? i : i;
                // use pickup-one: click slot then offhand slot
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
                mc().interactionManager.clickSlot(player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, player);
                cooldown = 4;
                return;
            }
        }
    }
}
