package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.ModeSetting;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.TradeOffer;

/**
 * AutoSell — singleplayer shops often use custom "sell" commands (data packs)
 * or wandering trader. When a sell-able stack is detected, drops/sells it via
 * the vanilla trade interact for wandering traders nearby.
 */
public class AutoSell extends Module {
    private final ModeSetting item = new ModeSetting("Item", "Rotten Flesh", "Bone", "Egg", "Feather");
    private int cd = 0;

    public AutoSell() {
        super("Auto Sell", "Sell junk to wandering traders automatically", Category.DONUT);
        addSettings(item);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 40;
        Item target = switch (item.getValue()) {
            case "Bone" -> Items.BONE;
            case "Egg" -> Items.EGG;
            case "Feather" -> Items.FEATHER;
            default -> Items.ROTTEN_FLESH;
        };
        var player = mc().player;
        if (player == null) return;

        // find wandering trader or villager within 8 blocks
        var list = player.getEntityWorld().getEntitiesByClass(
                net.minecraft.entity.passive.MerchantEntity.class,
                player.getBoundingBox().expand(8),
                e -> true);
        if (list.isEmpty()) return;
        var merchant = list.get(0);

        // find stack
        for (int i = 0; i < player.getInventory().getMainStacks().size(); i++) {
            var s = player.getInventory().getMainStacks().get(i);
            if (!s.isEmpty() && s.getItem() == target) {
                player.interact(merchant, net.minecraft.util.Hand.MAIN_HAND);
                Msg.info("Offered " + s.getCount() + "x " + target.getName().getString() + " to trader");
                cd = 100;
                return;
            }
        }
    }
}
