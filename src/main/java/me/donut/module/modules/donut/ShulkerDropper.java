package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import me.donut.util.Msg;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

/**
 * ShulkerDropper — helps inventory hygiene during long farm runs:
 * whenever a non-empty shulker box ends up in your main inventory,
 * it is flung onto the ground automatically (name-based whitelist).
 */
public class ShulkerDropper extends Module {
    private final BooleanSetting onlyNamed = new BooleanSetting("Named Only", true);
    private int cd = 0;

    public ShulkerDropper() {
        super("Shulker Dropper", "Drop non-empty shulker boxes automatically", Category.DONUT);
        addSettings(onlyNamed);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null) return;

        for (int i = 3; i < player.getInventory().getMainStacks().size(); i++) { // skip hotbar
            ItemStack s = player.getInventory().getMainStacks().get(i);
            if (s.isEmpty() || !(s.getItem() instanceof BlockItem bi)) continue;
            if (!bi.getBlock().toString().contains("shulker_box")) continue;

            if (onlyNamed.getValue() && !s.contains(net.minecraft.component.DataComponentTypes.CUSTOM_NAME)) {
                continue;
            }
            player.getInventory().getMainStacks().set(i, ItemStack.EMPTY);
            player.dropItem(s, true, false);
            Msg.info("Dropped shulker " + s.getName().getString());
            cd = 20;
            return;
        }
    }
}
