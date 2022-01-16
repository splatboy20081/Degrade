package xyz.elevated.frequency.check.impl.invalid;

import net.minecraft.server.v1_8_R3.EntityPlayer;
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

@CheckData(name = "Invalid (F)")
public final class InvalidF extends PositionCheck {

    public InvalidF(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(PositionUpdate positionUpdate) {
        Location from = positionUpdate.getFrom();
        Location to = positionUpdate.getTo();

        Player player = playerData.getBukkitPlayer();
        EntityPlayer entityPlayer = NmsUtil.getEntityPlayer(playerData);

        double deltaY = to.getY() - from.getY();

        boolean deltaModulo = deltaY % 0.015625 == 0.0;
        boolean lastGround = from.getY() % 0.015625 == 0.0;

        boolean step = deltaModulo && lastGround;

        double modifierJump = MathUtil.getPotionLevel(player, PotionEffectType.JUMP) * 0.1F;
        double expectedJumpMotion = 0.42F + modifierJump;

        boolean ground = entityPlayer.onGround;

        boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.TELEPORTING);
        boolean invalid = deltaY > expectedJumpMotion && !ground && !step;

        if (invalid && !exempt) fail();
        if (step && deltaY > 0.59F && !exempt) fail();
    }
}
