package me.donut.module.modules.misc;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.math.BlockPos;

/**
 * Cord Snapper — remembers your last coordinates every few seconds so that
 * on death you immediately get told where you died and can tp back with cheats
 * (singleplayer-cheats behavior).
 */
public class CordSnapper extends Module {
    private BlockPos lastPos = null;
    private long lastSave = 0;

    public CordSnapper() {
        super("Cord Snapper", "Remember coordinates before you die, tp back", Category.MISC);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;

        long now = System.currentTimeMillis();
        if (now - lastSave > 2000) {
            lastSave = now;
            lastPos = player.getBlockPos();
        }

        if (mc().currentScreen instanceof DeathScreen && lastPos != null) {
            var p = player.getBlockPos();
            if (p.getSquaredDistance(lastPos) > 4) {
                Msg.info("You died near " + fmt(p) + " — last safe pos " + fmt(lastPos)
                        + ". Run /tp " + lastPos.getX() + " " + lastPos.getY() + " " + lastPos.getZ());
                lastPos = null;
            }
        }
    }

    private String fmt(BlockPos pos) {
        return "X" + pos.getX() + " Y" + pos.getY() + " Z" + pos.getZ();
    }
}
