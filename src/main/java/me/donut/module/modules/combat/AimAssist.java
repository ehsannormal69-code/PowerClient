package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

/**
 * AimAssist — gently curves the player's view toward the nearest living
 * target within reach while the attack key is pressed.
 */
public class AimAssist extends Module {
    private final DoubleSetting range = new DoubleSetting("Range", 4.0, 1.0, 6.0, 0.5);
    private final DoubleSetting strength = new DoubleSetting("Strength", 0.15, 0.0, 1.0, 0.05);

    public AimAssist() {
        super("Aim Assist", "Softly aims at nearby targets while attacking", Category.COMBAT);
        addSettings(range, strength);
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        if (!mc().options.attackKey.isPressed()) return;

        List<LivingEntity> targets = player.getEntityWorld().getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(range.getValue()),
                e -> e != player && e.isAlive() && !e.isSpectator()
        );
        var target = targets.stream().min(Comparator.comparingDouble(e -> e.squaredDistanceTo(player)));
        if (target.isEmpty()) return;

        LivingEntity t = target.get();
        Vec3d aim = t.getEyePos().subtract(player.getEyePos());
        float targetYaw = (float) MathHelper.atan2(aim.z, aim.x) * 57.2957764f - 90f;
        float targetPitch = (float) (-MathHelper.atan2(aim.y, Math.sqrt(aim.x * aim.x + aim.z * aim.z)) * 57.2957764f);

        float s = strength.getValue().floatValue();
        float newYaw = player.getYaw() + MathHelper.wrapDegrees(targetYaw - player.getYaw()) * s;
        float newPitch = player.getPitch() + MathHelper.wrapDegrees(targetPitch - player.getPitch()) * s;
        player.setYaw(newYaw);
        player.setPitch(newPitch);
    }
}
