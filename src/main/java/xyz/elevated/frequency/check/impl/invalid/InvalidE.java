package xyz.elevated.frequency.check.impl.invalid;

import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "Invalid (E)")
public final class InvalidE extends PositionCheck {
    private double lastOffsetH = 0.0;
    private int buffer = 0;

    public InvalidE(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();
        double deltaZ = to.getZ() - from.getZ();

        double offsetH = MathUtil.magnitude(deltaX, deltaZ);
        double offsetY = Math.abs(deltaY);

        boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.VELOCITY);
        boolean touchingAir = playerData.getPositionManager().getTouchingAir().get();

        if (!exempt && touchingAir && offsetH > 0.005 && offsetY < 90.d) {
            double attributeSpeed = lastOffsetH * 0.91F + 0.02;

            boolean sprinting = playerData.getSprinting().get();
            if (sprinting) attributeSpeed += 0.0063;

            if (offsetH - attributeSpeed > 1e-12 && offsetH > 0.1 && attributeSpeed > 0.075) {
                if (++buffer > 5) {
                    fail();
                }
            } else {
                buffer = 0;
            }
        }

        lastOffsetH = offsetH;
    }
}
