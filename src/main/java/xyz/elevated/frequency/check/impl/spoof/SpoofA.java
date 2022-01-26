package xyz.elevated.frequency.check.impl.spoof;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.NmsUtil;

@CheckData(name = "Spoof (A)", threshold = 6)
public final class SpoofA extends PositionCheck {

    public SpoofA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        if(isExempt(ExemptType.TELEPORTING, ExemptType.VEHICLE)) return;

        if (NmsUtil.getEntityPlayer(playerData).locY % 0.015625 == 0 && !NmsUtil.getEntityPlayer(playerData).onGround) {

            fail();

        }

    }
}
