package xyz.elevated.frequency.check.impl.aimassist;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.RotationCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.RotationUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "AimAssist (D)")
public final class AimAssistD extends RotationCheck {

    private float lastDeltaPitch = 0.0f;
    private boolean applied = false;

    private int rotations = 0;
    private final long[] grid = new long[10];

    public AimAssistD(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(RotationUpdate rotationUpdate) {
        long now = System.currentTimeMillis();

        float deltaYaw = rotationUpdate.getDeltaYaw();
        float deltaPitch = rotationUpdate.getDeltaPitch();

        boolean cinematic = playerData.getCinematic().get();
        boolean attacking = now - playerData.getActionManager().getLastAttack() < 500L;

        long deviation = getDeviation(deltaPitch);

        ++rotations;
        grid[rotations % grid.length] = deviation;

        // If the player wasn't using cinematic, where attacking and weren't spamming their aim
        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 30.f && deltaPitch < 30.f && !cinematic && attacking) {
            boolean reached = rotations > grid.length;

            // If the rotations made were greater than the gcd length
            if (reached) {
                double deviationMax = 0;

                // Get the max deviation from the gcd log
                for (double l : grid) {
                    if (deviation != 0 && l != 0)
                        deviationMax = Math.max(Math.max(l, deviation) % Math.min(l, deviation), deviationMax);
                }

                // If both the deviation and the max deviation were greater than 0,9
                if (deviationMax > 0.0 && deviation > 0.0) {
                    fail();

                    applied = false;
                }
            }
        }

        lastDeltaPitch = deltaPitch;
    }

    // Get the GCD from the stored rotations and return a result whenever applied isn't false.
    private long getDeviation(float deltaPitch) {
        long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
        long previousExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

        long result = applied ? MathUtil.getGcd(expandedPitch, previousExpandedPitch) : 0;

        if (applied) {
            applied = false;

            return result;
        }

        return 0L;
    }
}
