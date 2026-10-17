package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * BoneOrder — compacts bone/bonemeal stacks in an opened container,
 * merging smaller stacks into bigger ones one swap per run.
 */
public class BoneOrder extends Module {
    private int cd = 0;

    public BoneOrder() {
        super("Bone Order", "Compacts bone stacks in open container", Category.DONUT);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null) return;
        if (!(mc().currentScreen instanceof HandledScreen<?>)) return;
        ScreenHandler handler = player.currentScreenHandler;
        if (!(handler instanceof GenericContainerScreenHandler gh)) return;

        record Ref(int slot, int count) {}
        List<Ref> refs = new ArrayList<>();
        int rows = gh.getRows();
        for (int i = 0; i < rows * 9; i++) {
            ItemStack s = gh.getSlot(i).getStack();
            if (!s.isEmpty() && (s.getItem() == Items.BONE || s.getItem() == Items.BONE_MEAL)) {
                refs.add(new Ref(i, s.getCount()));
            }
        }
        if (refs.size() < 2) return;

        refs.sort(Comparator.comparingInt(Ref::count).reversed());
        Ref big = refs.get(0);
        Ref small = refs.get(refs.size() - 1);
        if (big.count() + small.count() <= 64) {
            var mgr = mc().interactionManager;
            int sync = handler.syncId;
            mgr.clickSlot(sync, big.slot(), 0, SlotActionType.PICKUP, player);
            mgr.clickSlot(sync, small.slot(), 0, SlotActionType.PICKUP, player);
            mgr.clickSlot(sync, big.slot(), 0, SlotActionType.PICKUP, player);
            cd = 4;
            Msg.info("Compacted bone stack");
        }
    }
}
