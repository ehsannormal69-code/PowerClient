package me.donut.module.modules.donut;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.BooleanSetting;
import me.donut.util.Msg;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.BlockItem;

/**
 * AntiTrap — when standing inside a 1x1 hole surrounded on all sides,
 * uses blocks from the hotbar to seal the hole behind you (crystal-pvp safety).
 */
public class AntiTrap extends Module {
    private final BooleanSetting preferObsidian = new BooleanSetting("Prefer Obsidian", true);
    private int cd = 0;

    public AntiTrap() {
        super("Anti Trap", "Blocks escape holes automatically", Category.DONUT);
        addSettings(preferObsidian);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        var player = mc().player;
        if (player == null) return;

        // Check if we're in a "hole": feet and head surrounded (4 sides)
        BlockPos feet = player.getBlockPos();
        boolean trapped = true;
        for (Direction d : Direction.Type.HORIZONTAL) {
            if (player.getEntityWorld().getBlockState(feet.offset(d)).isAir()) { trapped = false; break; }
        }
        if (!trapped) return;
        if (player.getEntityWorld().getBlockState(feet.up()).isAir()) {
            // Hole is open upward — either they trapped us, or we're fine; only act if 2+ mobs/players nearby
        } else return;

        // find a block in hotbar
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            var s = player.getInventory().getMainStacks().get(i);
            if (s.getItem() instanceof BlockItem b) {
                if (preferObsidian.getValue() && s.getItem() != net.minecraft.item.Items.OBSIDIAN) continue;
                slot = i;
                break;
            }
        }
        if (slot == -1) return;

        player.getInventory().setSelectedSlot(slot);
        // place above head
        BlockPos head = feet.up();
        BlockPos face = head.up();
        mc().interactionManager.interactBlock(
                player, net.minecraft.util.Hand.MAIN_HAND,
                new BlockHitResult(Vec3d.ofCenter(face), Direction.DOWN, face.down(), false)
        );
        player.swingHand(net.minecraft.util.Hand.MAIN_HAND, true);
        cd = 4;
    }
}
