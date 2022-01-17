package xyz.elevated.frequency.check.impl.invalidposition;

import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "InvalidPosition")
public final class InvalidPosition extends PositionCheck {

  private double lastHorizontalDistance, buffer;

  public InvalidPosition(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(PositionUpdate positionUpdate) {
    Location from = positionUpdate.getFrom();
    Location to = positionUpdate.getTo();

    double deltaX = to.getX() - from.getX();
    double deltaY = to.getY() - from.getY();
    double deltaZ = to.getZ() - from.getZ();

    double horizontalDistance = MathUtil.magnitude(deltaX, deltaZ);
    double acceleration = Math.abs(horizontalDistance - lastHorizontalDistance);

    boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.VELOCITY);
    boolean sprinting = playerData.getSprinting().get();

    if (exempt || !sprinting) return;

    if (acceleration > 0.3) {
      buffer += 0.5;

      if (buffer > 1.5) {
        fail();

        buffer = 0;
      }
    } else {
      buffer = Math.max(buffer - 0.125, 0);
    }

    // It's impossible to make that small of a movement without it being rounded to 0
    if (deltaY >= 0.0 && horizontalDistance < 0.00001 && acceleration == 0.0) {
      fail();
    }

    lastHorizontalDistance = horizontalDistance;
  }
}
