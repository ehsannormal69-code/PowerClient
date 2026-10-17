package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * PearlBoost — clears the ender-pearl cooldown instantly (SP-cheats style),
 * letting you chain pearl throws without waiting the vanilla 20-tick window.
 */
public class PearlBoost extends Module {
    private final BooleanSetting alwaysInstant = new BooleanSetting("Instant CD", true);

    public PearlBoost() {
        super("Pearl Boost", "No ender pearl cooldown", Category.MISC);
        addSettings(alwaysInstant);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (!alwaysInstant.getValue()) return;

        var cd = player.getItemCooldownManager();
        Identifier pearlId = Registries.ITEM.getId(Items.ENDER_PEARL);
        ItemStack probe = new ItemStack(Items.ENDER_PEARL);
        if (cd.isCoolingDown(probe)) {
            cd.remove(pearlId);
        }
    }
}
