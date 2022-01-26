package xyz.elevated.frequency.check.impl.jesus;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.NmsUtil;

@CheckData(name = "Jesus (B)")
public final class JesusB extends PositionCheck {

    public JesusB(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {

        boolean touchingLiquid = playerData.getPositionManager().getTouchingLiquid().get() && (positionUpdate.getFrom().getBlock().isLiquid() || positionUpdate.getTo().getBlock().isLiquid() && NmsUtil.getEntityPlayer(playerData).inWater);

        if((NmsUtil.getMotion(playerData).getY() > 0.3 || NmsUtil.getMotion(playerData).getY() < -0.3) && touchingLiquid)
            fail();

    }
}
