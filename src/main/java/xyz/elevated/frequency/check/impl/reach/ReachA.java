package xyz.elevated.frequency.check.impl.reach;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInUseEntity;

@CheckData(name = "Reach (A)")
public final class ReachA extends PacketCheck {

    public ReachA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {

        if (object instanceof WrappedPlayInUseEntity && !isExempt(ExemptType.TPS)) {

            if(((WrappedPlayInUseEntity) object).getAction().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {

               // double dist = MathUtil.getMagnitude(playerData.getBukkitPlayer().getLocation(), ((WrappedPlayInUseEntity) object).getVector().toLocation());

            }

        }

    }
}
