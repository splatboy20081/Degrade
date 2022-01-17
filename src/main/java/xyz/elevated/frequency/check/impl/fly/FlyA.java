package xyz.elevated.frequency.check.impl.fly;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.NmsUtil;

@CheckData(name = "Fly (A)")
public final class FlyA extends PositionCheck {

    private double buffer;
    private int airTicks;

    public FlyA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        // Get the locations from the position update
        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        // Get the entity player from the nms util
        EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData);

        // If the posY of the player is modulo by (1/64) he's on ground.
        boolean clientGround = entityPlayer.onGround;
        boolean serverGround = to.getY() % 0.015625 == 0.0 && from.getY() % 0.015625 == 0.0;

        boolean touchingAir = playerData.getPositionManager().getTouchingAir().get();
        boolean illegal = playerData.getPositionManager().getTouchingClimbable().get() || playerData.getPositionManager().getTouchingLiquid().get();

        if(touchingAir) {
            ++airTicks;
        } else {
            airTicks = 0;
        }

        //Added check for air ticks, because this falses on some positions when you are in air only for 1-2 ticks
        if (!illegal && clientGround != serverGround && airTicks > 2) {
            if (++buffer > 4) fail();
        } else {
            buffer = 0;
        }
    }
}
