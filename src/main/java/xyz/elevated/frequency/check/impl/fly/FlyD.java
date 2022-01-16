package xyz.elevated.frequency.check.impl.fly;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "Fly (D)")
public final class FlyD extends PositionCheck {

    private int ticks = 0;
    private double total = 0.0d, buffer = 0.0d;

    public FlyD(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();
        double deltaZ = to.getZ() - from.getZ();

        int modifierJump = MathUtil.getPotionLevel(playerData.getBukkitPlayer(), PotionEffectType.JUMP);

        double offset = MathUtil.magnitude(deltaX, deltaZ);
        double threshold = modifierJump > 0 ? 1.55220341408 + (Math.pow(modifierJump + 4.2, 2D) / 16D) : 1.25220341408;

        boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.VELOCITY);
        boolean touchingAir = playerData.getPositionManager().getTouchingAir().get();

        if (touchingAir && !exempt) {
            ++ticks;

            if (ticks > 7 && offset > 0.1) {
                total += deltaY;

                if (total > threshold) {
                    buffer += 0.25;

                    if (buffer > 1.5) {
                        fail();
                    }
                } else {
                    buffer = 0.0;
                }
            } else {
                total = 0.0;
                buffer = 0.0;
            }
        } else {
            ticks = 0;
        }
    }
}
