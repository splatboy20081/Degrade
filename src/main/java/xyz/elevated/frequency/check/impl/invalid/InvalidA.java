package xyz.elevated.frequency.check.impl.invalid;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.util.NmsUtil;

@CheckData(name = "Invalid (A)")
public final class InvalidA extends PositionCheck {

    public InvalidA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        // Get the locations from the position update
        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        // Get the delta of the positions and the velocity of the player
        double deltaY = to.getY() - from.getY();
        double velocityY = playerData.getVelocityManager().getMaxVertical();

        // Calculate their max Y according to the formula baseJump + (amplifier * 0.1)
        int amplifierJump = MathUtil.getPotionLevel(playerData.getBukkitPlayer(), PotionEffectType.JUMP);

        double motionY = NmsUtil.getMotion(playerData).getY();
        double threshold = amplifierJump > 0 ? 0.42 + amplifierJump * 0.1 : 0.42;

        // Make sure the player isn't exempt
        boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.TPS);

        // If the player is ascending higher than the threshold and has no velocity
        if (velocityY == 0.0 && deltaY > threshold && !exempt && motionY == 0.0) {
            fail();
        }
    }
}
