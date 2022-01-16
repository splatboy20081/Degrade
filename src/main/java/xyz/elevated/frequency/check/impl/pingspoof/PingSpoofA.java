package xyz.elevated.frequency.check.impl.pingspoof;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "PingSpoof (A)")
public final class PingSpoofA extends PacketCheck {

    public PingSpoofA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInFlying) {
            long transactionPing = playerData.getTransactionPing().get();
            long keepAlivePing = playerData.getKeepAlivePing().get();

            boolean joined = playerData.getTicks().get() - playerData.getJoined().get() < 10;
            boolean exempt = isExempt(ExemptType.LAGGING, ExemptType.TELEPORTING, ExemptType.TPS, ExemptType.CHUNK);

            if (!exempt && !joined && transactionPing > keepAlivePing && Math.abs(transactionPing - keepAlivePing) > 50) fail();
        }
    }
}
