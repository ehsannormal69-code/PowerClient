package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.Msg;
import me.donut.util.SSP;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

/**
 * Bone Dropper — keeps a configured number of bone/bone-meal stacks in the
 * container being targeted and ejects only the excess into the world.
 */
public class BoneDropper extends Module {
    private final DoubleSetting keepStacks = new DoubleSetting("Keep Stacks", 4.0, 0.0, 27.0, 1.0);
    private int cd;

    public BoneDropper() {
        super("Bone Dropper", "Ejects surplus bones from looked-at chest", Category.DONUT);
        addSettings(keepStacks);
    }

    @Override
    public void onTick() {
        if (cd-- > 0) return;
        cd = 100;
        if (mc().player == null || !(mc().crosshairTarget instanceof BlockHitResult hit)
                || mc().crosshairTarget.getType() != HitResult.Type.BLOCK) return;
        BlockPos pos = hit.getBlockPos().toImmutable();

        SSP.onServer(() -> {
            var world = SSP.playerWorld();
            var player = SSP.player();
            if (world == null || player == null || !(world.getBlockEntity(pos) instanceof LootableInventory inventory)) return;
            inventory.generateLoot(player);

            int retained = 0;
            int dropped = 0;
            int limit = keepStacks.getValue().intValue() * 64;
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack stack = inventory.getStack(slot);
                if (stack.isEmpty() || (stack.getItem() != Items.BONE && stack.getItem() != Items.BONE_MEAL)) continue;

                int remainingCapacity = Math.max(0, limit - retained);
                int dropCount = Math.max(0, stack.getCount() - remainingCapacity);
                retained += Math.min(stack.getCount(), remainingCapacity);
                if (dropCount == 0) continue;

                ItemStack droppedStack = stack.copyWithCount(dropCount);
                stack.decrement(dropCount);
                inventory.setStack(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
                ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5, droppedStack);
                item.setPickupDelay(40);
                world.spawnEntity(item);
                dropped += dropCount;
            }
            Msg.serverLog(dropped == 0
                    ? "No surplus bones at " + pos.toShortString()
                    : "Dropped " + dropped + " surplus bone items at " + pos.toShortString());
        });
    }
}
