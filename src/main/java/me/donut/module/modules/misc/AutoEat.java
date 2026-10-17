package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Auto Eat — selects food from the hotbar and holds use when hunger
 * is below the threshold.
 */
public class AutoEat extends Module {
    private final DoubleSetting hunger = new DoubleSetting("Hunger <=", 14.0, 1.0, 20.0, 1.0);

    private int lastSlot = -1;
    private boolean eating = false;

    public AutoEat() {
        super("Auto Eat", "Automatically eats food when hungry", Category.MISC);
        addSettings(hunger);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;

        boolean needFood = player.getHungerManager().getFoodLevel() <= hunger.getValue();

        if (eating) {
            if (!needFood || player.getHungerManager().getFoodLevel() >= 20) {
                stop(player);
                return;
            }
            mc().options.useKey.setPressed(true);
            return;
        }

        if (!needFood) return;
        // player busy doing anything else? hold off
        if (player.isUsingItem() || mc().currentScreen != null) return;

        int slot = findFood(player);
        if (slot == -1) return;

        if (slot != player.getInventory().getSelectedSlot()) {
            lastSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(slot);
        }
        mc().interactionManager.interactItem(player, Hand.MAIN_HAND);
        player.swingHand(Hand.MAIN_HAND, true);
        eating = true;
        mc().options.useKey.setPressed(true);
    }

    private void stop(net.minecraft.client.network.ClientPlayerEntity player) {
        mc().options.useKey.setPressed(false);
        if (lastSlot >= 0) player.getInventory().setSelectedSlot(lastSlot);
        lastSlot = -1;
        eating = false;
    }

    private int findFood(net.minecraft.client.network.ClientPlayerEntity player) {
        int best = -1;
        float bestNutrition = 0f;
        for (int i = 0; i < 9; i++) {
            ItemStack s = player.getInventory().getMainStacks().get(i);
            FoodComponent food = s.getItem().getComponents().get(DataComponentTypes.FOOD);
            if (food == null) continue;
            // skip rotten flesh/spider eye etc — simple heuristic: nutrition >= 4
            if (food.nutrition() < 4) continue;
            if (food.nutrition() > bestNutrition) {
                bestNutrition = food.nutrition();
                best = i;
            }
        }
        return best;
    }
}
