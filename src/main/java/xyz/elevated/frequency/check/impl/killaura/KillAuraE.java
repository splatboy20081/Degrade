package xyz.elevated.frequency.check.impl.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInUseEntity;

@CheckData(name = "KillAura (E)")
public final class KillAuraE extends PacketCheck {

    private int movements = 0, lastMovements = 0, total = 0, invalid = 0;

    public KillAuraE(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInUseEntity) {
            WrappedPlayInUseEntity wrapper = (WrappedPlayInUseEntity) object;

            if (wrapper.getAction() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                boolean proper = playerData.getCps().get() > 7.2 && movements < 4 && lastMovements < 4;

                if (proper) {
                    boolean flag = movements == lastMovements;

                    if (flag) {
                        ++invalid;
                    }

                    if (++total == 30) {

                        if (invalid > 28)
                            fail();

                        total = 0;
                    }
                }

                lastMovements = movements;
                movements = 0;
            }
        } else if (object instanceof WrappedPlayInFlying) {
            ++movements;
        }
    }
}
