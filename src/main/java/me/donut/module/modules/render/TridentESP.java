package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import net.minecraft.entity.projectile.TridentEntity;

import java.util.List;

/**
 * TridentESP — shows every thrown trident in the world with owner,
 * position and whether it's still flying or stuck.
 */
public class TridentESP extends Module {
    private int cd = 0;

    public TridentESP() {
        super("Trident ESP", "Track thrown tridents", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 5;
        var mc = mc();
        if (mc.player == null || mc.world == null) return;

        List<TridentEntity> tridents = mc.world.getEntitiesByClass(
                TridentEntity.class,
                mc.player.getBoundingBox().expand(64),
                t -> true
        );
        if (tridents.isEmpty()) {
            HudCollected.put("trident", "No tridents nearby");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (TridentEntity t : tridents) {
            double d = mc.player.getEyePos().distanceTo(t.getEntityPos());
            String owner = t.getOwner() != null ? t.getOwner().getName().getString() : "?";
            sb.append(owner).append("'s trident ")
              .append((int) d).append("m ")
              .append(t.isNoClip() ? "(loyal-return)" : "(flying)")
              .append("  ");
        }
        HudCollected.put("trident", sb.toString().trim());
    }

    @Override
    protected void onDisable() {
        HudCollected.put("trident", null);
    }
}
