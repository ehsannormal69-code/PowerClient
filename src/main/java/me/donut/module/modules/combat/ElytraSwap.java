package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * ElytraSwap — hot-swaps between glider (elytra) and a chestplate with one press.
 */
public class ElytraSwap extends Module {
    private boolean requested = false;
    private int cd = 0;

    public ElytraSwap() {
        super("Elytra Swap", "Swap chestplate <-> elytra on toggle", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        requested = true; // trigger swap once when toggled
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null || player.currentScreenHandler == null) return;
        if (!requested) return;
        requested = false;

        ItemStack chest = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        boolean hasElytraOn = !chest.isEmpty() && chest.contains(DataComponentTypes.GLIDER);

        for (int i = 0; i < player.getInventory().getMainStacks().size(); i++) {
            ItemStack s = player.getInventory().getMainStacks().get(i);
            if (s.isEmpty()) continue;
            boolean isElytra = s.contains(DataComponentTypes.GLIDER);
            boolean isChestplate = s.getItem() == Items.NETHERITE_CHESTPLATE
                    || s.getItem() == Items.DIAMOND_CHESTPLATE
                    || s.getItem() == Items.IRON_CHESTPLATE;
            if (!hasElytraOn && isElytra) {
                int slot = i < 9 ? i + 36 : (i - 9) + 9;
                swapChest(player, slot);
                Msg.info("Swapped to elytra");
                cd = 5;
                return;
            } else if (hasElytraOn && isChestplate) {
                int slot = i < 9 ? i + 36 : (i - 9) + 9;
                swapChest(player, slot);
                Msg.info("Swapped to chestplate");
                cd = 5;
                return;
            }
        }
        Msg.warn("No matching chest/elytra item in inventory");
        setEnabled(false);
    }

    private void swapChest(net.minecraft.client.network.ClientPlayerEntity player, int invSlot) {
        var mgr = mc().interactionManager;
        int sync = player.currentScreenHandler.syncId;
        mgr.clickSlot(sync, 6, 0, SlotActionType.PICKUP, player);
        mgr.clickSlot(sync, invSlot, 0, SlotActionType.PICKUP, player);
        mgr.clickSlot(sync, 6, 0, SlotActionType.PICKUP, player);
    }

    @Override
    protected void onDisable() {
        // one-shot module; nothing persistent
    }
}
