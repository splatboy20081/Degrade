package xyz.elevated.frequency.check.impl.fly;

import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "Fly (C)")
public final class FlyC extends PositionCheck {

  private double lastDeltaY, buffer;
  private int ticks;

  public FlyC(PlayerData playerData) {
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
    double acceleration = Math.abs(deltaY - lastDeltaY);

    boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.VELOCITY);
    boolean touchingAir = playerData.getPositionManager().getTouchingAir().get();

    if (!exempt && touchingAir) {
      ++ticks;

      if (ticks > 6 && horizontalDistance > 0.1 && (deltaY == 0.0 || acceleration == 0.0)) {
        buffer += 0.25;

        if (buffer > 0.75) fail();
      } else {
        buffer = Math.max(buffer - 0.12, 0.0);
      }
    } else {
      buffer = 0;
      ticks = 0;
    }

    lastDeltaY = deltaY;
  }
}
