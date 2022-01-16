package xyz.elevated.frequency.check.impl.badpackets;

import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "BadPackets (Q)", threshold = 2)
public final class BadPacketsQ extends PositionCheck {

    public BadPacketsQ(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {

        boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.TPS, ExemptType.VOID);

        if(exempt) return;

        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();

        double horizontalDistance = MathUtil.magnitude(deltaX, deltaZ);

        if (horizontalDistance > 5 || horizontalDistance < -5) {

            fail();

        }

    }
}
