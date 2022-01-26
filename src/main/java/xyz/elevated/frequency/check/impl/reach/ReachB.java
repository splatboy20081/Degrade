package xyz.elevated.frequency.check.impl.reach;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.util.EvictingList;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInUseEntity;

import java.util.ArrayList;

@CheckData(name = "Reach (B)")
public final class ReachB extends PacketCheck {

    private static final double ALLOWED_REACH = 3.6;
    private static final double ALLOWED_REACH_SQUARED = Math.pow(ALLOWED_REACH + 0.3, 2.0);

    /**
     * The previous amounts of reach that a player has got.
     */
    private final EvictingList<Double> previousReach;

    public ReachB(PlayerData playerData) {
        super(playerData);

        this.previousReach = new EvictingList<>(10);

    }

    @Override
    public void process(Object object) {

        if (object instanceof WrappedPlayInUseEntity && !isExempt(ExemptType.TPS)) {

            if(((WrappedPlayInUseEntity) object).getAction().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {

                final double reachSquared = getDistanceSquaredXZ(playerData.getBukkitPlayer().getLocation(),
                        playerData.getTarget().get().getLocation());

                previousReach.add(reachSquared);

                // How far the player's reach is from the allowed value.
                final double reachOffset = reachSquared - ALLOWED_REACH_SQUARED;

                if (getAverageSquaredReach() > ALLOWED_REACH_SQUARED && reachOffset > 0.2) {

                    fail();

                    previousReach.clear();


                }


            }

        }

    }

    public static double getDistanceSquaredXZ(Location from, Location to) {
        final double diffX = Math.abs(from.getX() - to.getX()), diffY = Math.abs(from.getY() - to.getY());
        return diffX * diffX + diffY * diffY;
    }

    /**
     * @return The average squared reach of a player.
     */
    public double getAverageSquaredReach() {
        if (previousReach.size() == 0) {
            return 0.0;
        }

        double totalReach = 0.0;
        for (double reach : previousReach) {
            totalReach += reach;
        }

        return totalReach / previousReach.size();
    }

}
