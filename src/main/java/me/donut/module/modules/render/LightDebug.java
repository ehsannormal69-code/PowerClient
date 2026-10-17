package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

/**
 * LightDebug — surfaces raw light values (block light + sky light) for the
 * player's feet and the looked-at block: useful for anti-mob-farm design and
 * debugging mob-proofing of builds.
 */
public class LightDebug extends Module {
    private int cd = 0;

    public LightDebug() {
        super("Light Debug", "Show light levels for lighting-proof builds", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 5;
        var mc = mc();
        var world = mc.world;
        var player = mc.player;
        if (world == null || player == null) return;

        BlockPos feet = player.getBlockPos();
        int blockLight = world.getLightLevel(LightType.BLOCK, feet);
        int skyLight = world.getLightLevel(LightType.SKY, feet);

        String extra = "";
        if (mc.crosshairTarget instanceof BlockHitResult bhr && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos target = bhr.getBlockPos();
            extra = String.format(" | Target %d,%d,%d: bl %d sky %d",
                    target.getX(), target.getY(), target.getZ(),
                    world.getLightLevel(LightType.BLOCK, target),
                    world.getLightLevel(LightType.SKY, target));
        }
        HudCollected.put("light",
                String.format("Feet light: block %d sky %d%s", blockLight, skyLight, extra));
    }

    @Override
    protected void onDisable() {
        HudCollected.put("light", null);
    }
}
