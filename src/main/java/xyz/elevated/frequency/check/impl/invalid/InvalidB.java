package xyz.elevated.frequency.check.impl.invalid;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.data.impl.PositionManager;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.util.NmsUtil;

@CheckData(name = "Invalid (B)")
public final class InvalidB extends PositionCheck {

  public InvalidB(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(PositionUpdate positionUpdate) {
    PositionManager positionManager = playerData.getPositionManager();
    EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData);

    // Get the locations from the position update
    Location from = positionUpdate.getFrom();
    Location to = positionUpdate.getTo();

    // Get the deltas for each axis
    double deltaX = to.getX() - from.getX();
    double deltaY = to.getY() - from.getY();
    double deltaZ = to.getZ() - from.getZ();

    // Get the right circumstances. We don't want to run the check on these as it increases the
    // margin of error
    boolean environment =
        !positionManager.getTouchingAir().get()
            && !positionManager.getTouchingHalfBlock().get()
            && !positionManager.getTouchingLiquid().get()
            && positionUpdate.isOnGround();

    // We don't want the check to run when the player is on the void or when he's receiving velocity
    boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.VOID, ExemptType.VELOCITY);

    /*
     * The player should never exceed the distance of their basic head height. For our case we could
     * simply say the threshold was 0.6 but for the sake of being more accurate and more descriptive in
     * the source code, we will the simple formula of (headHeight - 1.0) which for the player when standing should
     * output the number 0.6. And just like that, we have a basic threshold for our check.
     */
    double threshold = entityPlayer.getHeadHeight() - 1.0;

    /*
     * There is no way for the player go beyond their basic head height and yet still remain saying they're
     * on ground. This should fix a couple of possible cheats using ground status without having many possibilities
     * for a false positive. Thus, we're checking if the player is also on ground and exceeding the threshold
     */
    if (deltaY > threshold && environment && !exempt) {
      double horizontalDistance = MathUtil.magnitude(deltaX, deltaZ);

      // Making sure the player is actually moving
      if (horizontalDistance > 0.1) fail();
    }
  }
}
