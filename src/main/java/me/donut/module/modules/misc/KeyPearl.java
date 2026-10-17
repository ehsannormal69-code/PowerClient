package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/**
 * Key Pearl — throws an ender pearl on the bound key press,
 * conserving the pearl in your inventory until thrown.
 */
public class KeyPearl extends Module {
    private boolean thrown = false;
    private int cd = 0;

    public KeyPearl() {
        super("Key Pearl", "Throw pearls on demand with a key press", Category.MISC);
    }

    @Override
    protected void onEnable() {
        thrown = true; // throw once per toggle
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null || !thrown) return;
        thrown = false;

        // prefer offhand pearl
        if (player.getOffHandStack().getItem() == Items.ENDER_PEARL) {
            mc().interactionManager.interactItem(player, Hand.OFF_HAND);
            player.swingHand(Hand.OFF_HAND, true);
            cd = 10;
            return;
        }

        int oldSlot = player.getInventory().getSelectedSlot();
        for (int i = 0; i < 9; i++) {
            ItemStack s = player.getInventory().getMainStacks().get(i);
            if (!s.isEmpty() && s.getItem() == Items.ENDER_PEARL) {
                player.getInventory().setSelectedSlot(i);
                mc().interactionManager.interactItem(player, Hand.MAIN_HAND);
                player.swingHand(Hand.MAIN_HAND, true);
                player.getInventory().setSelectedSlot(oldSlot);
                cd = 10;
                return;
            }
        }
        Msg.warn("No ender pearls in hotbar/offhand");
    }
}
