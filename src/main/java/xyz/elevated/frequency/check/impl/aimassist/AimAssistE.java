package xyz.elevated.frequency.check.impl.aimassist;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.RotationCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.RotationUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "AimAssist (E)")
public final class AimAssistE extends RotationCheck {
  private float lastDeltaYaw, lastDeltaPitch;
  private int buffer;

  private static final double MODULO_THRESHOLD = 90F;
  private static final double LINEAR_THRESHOLD = 0.1F;

  public AimAssistE(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(RotationUpdate rotationUpdate) {
    int now = playerData.getTicks().get();

    // Get the deltas from the rotation update
    float deltaYaw = rotationUpdate.getDeltaYaw();
    float deltaPitch = rotationUpdate.getDeltaPitch();

    // Grab the gcd using an expander.
    double divisorYaw =
        MathUtil.getGcd(
            (long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
    double divisorPitch =
        MathUtil.getGcd(
            (long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

    // Get the constant for both rotation updates by dividing by the expander
    double constantYaw = divisorYaw / MathUtil.EXPANDER;
    double constantPitch = divisorPitch / MathUtil.EXPANDER;

    // Get the estimated mouse delta from the constant
    double currentX = deltaYaw / constantYaw;
    double currentY = deltaPitch / constantPitch;

    // Get the estimated mouse delta from the old rotations using the new constant
    double previousX = lastDeltaYaw / constantYaw;
    double previousY = lastDeltaPitch / constantPitch;

    // Make sure the player is attacking or placing to filter out the check
    boolean action =
        now - playerData.getActionManager().getLastAttack() < 3
            || now - playerData.getActionManager().getLastPlace() < 3;

    // Make sure the rotation is not very large and not equal to zero and get the modulo of the xys
    if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f && action) {
      double moduloX = currentX % previousX;
      double moduloY = currentY % previousY;

      // Get the floor delta of the the modulos
      double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
      double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

      // Impossible to have a different constant in two rotations
      boolean invalidX = moduloX > MODULO_THRESHOLD && floorModuloX > LINEAR_THRESHOLD;
      boolean invalidY = moduloY > MODULO_THRESHOLD && floorModuloY > LINEAR_THRESHOLD;

      if (invalidX && invalidY) {
        buffer = Math.min(buffer + 1, 200);

        if (buffer > 6) fail();
      } else {
        buffer = 0;
      }
    }

    lastDeltaYaw = deltaYaw;
    lastDeltaPitch = deltaPitch;
  }
}
