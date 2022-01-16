package xyz.elevated.frequency.check.impl.invalid;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.PositionUpdate;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.util.NmsUtil;

@CheckData(name = "Invalid (C)")
public final class InvalidC extends PositionCheck {

    private double buffer = 0.0d;
    private int ticks = 0;

    public InvalidC(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData);

        double deltaX = to.getX() - from.getZ();
        double deltaZ = to.getZ() - from.getZ();

        double offset = MathUtil.magnitude(deltaX, deltaZ);
        double velocity = entityPlayer.motX + entityPlayer.motY;

        boolean onGround = positionUpdate.isOnGround();

        if (!onGround) {
            boolean invalid = ++ticks > 8 && offset > 0.3 && velocity == 0.0;

            if (invalid) {
                buffer += 0.5;

                if (buffer > 1.5) {
                    fail();
                }
            } else {
                buffer = Math.max(buffer - 0.025, 0);
            }
        } else {
            ticks = 0;
        }
    }
}
