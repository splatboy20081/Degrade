package xyz.elevated.frequency.check.impl.killaura;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "KillAura (F)")
public final class KillAuraF extends PacketCheck {

    private double lastPosX = 0.0d, lastPosZ = 0.0d, lastHorizontalDistance = 0.0d;
    private float lastYaw = 0L, lastPitch = 0L;

    public KillAuraF(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInFlying) {
            WrappedPlayInFlying wrapper = (WrappedPlayInFlying) object;

            if (!wrapper.hasLook() || !wrapper.hasPos()) return;

            double posX = wrapper.getX();
            double posZ = wrapper.getZ();

            float yaw = wrapper.getYaw();
            float pitch = wrapper.getPitch();

            double horizontalDistance = MathUtil.magnitude(posX - lastPosX, posZ - lastPosZ);

            // Player moved
            if (horizontalDistance > 0.0) {
                float deltaYaw = Math.abs(yaw - lastYaw);
                float deltaPitch = Math.abs(pitch - lastPitch);

                boolean attacking = playerData.getActionManager().getAttacking().get();
                double acceleration = Math.abs(horizontalDistance - lastHorizontalDistance);

                // Player made a large head rotation and didn't accelerate / decelerate which is impossible
                if (acceleration < 1e-02 && deltaYaw > 30.f && deltaPitch > 15.f && attacking) {
                    fail();
                }
            }

            lastHorizontalDistance = horizontalDistance;
            lastYaw = yaw;
            lastPitch = pitch;
            lastPosX = posX;
            lastPosZ = posZ;
        }
    }
}
