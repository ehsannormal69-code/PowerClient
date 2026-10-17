package me.donut.module.modules.crystal;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import me.donut.util.SSP;
import net.minecraft.entity.decoration.EndCrystalEntity;

import java.util.Comparator;
import java.util.List;

/**
 * Auto Crystal — singleplayer crystal PvP: attacks the closest end crystal
 * in range automatically using the server player's own attack.
 */
public class AutoCrystal extends Module {
    private final DoubleSetting range = new DoubleSetting("Range", 4.5, 1.0, 12.0, 0.5);
    private final DoubleSetting delayTicks = new DoubleSetting("Delay", 2.0, 0.0, 20.0, 1.0);

    private int cd = 0;

    public AutoCrystal() {
        super("Auto Crystal", "Hits nearby end crystals automatically", Category.CRYSTAL);
        addSettings(range, delayTicks);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        // act on the integrated server thread for exact, cheats-on behavior
        SSP.onServer(() -> {
            var sPlayer = SSP.player();
            var sWorld = SSP.playerWorld();
            if (sPlayer == null || sWorld == null) return;

            List<EndCrystalEntity> crystals = sWorld.getEntitiesByClass(
                    EndCrystalEntity.class,
                    sPlayer.getBoundingBox().expand(range.getValue()),
                    e -> true
            );
            var target = crystals.stream()
                    .min(Comparator.comparingDouble(e -> sPlayer.squaredDistanceTo(e)));
            target.ifPresent(crystal -> {
                double d = sPlayer.getEyePos().distanceTo(crystal.getEntityPos());
                if (d <= range.getValue() + 0.5) {
                    sPlayer.attack(crystal);
                }
            });
        });
        cd = delayTicks.getValue().intValue();
    }
}
