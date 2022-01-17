package xyz.elevated.frequency.check.impl.inventory;

import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInClientCommand;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "Inventory (B)", threshold = 3)
public final class InventoryB extends PacketCheck {

    private long lastFlying = System.currentTimeMillis();
    private boolean inventory;
    private int buffer;

    public InventoryB(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInClientCommand) {
            WrappedPlayInClientCommand wrapper = (WrappedPlayInClientCommand) object;

            achievement: {
                if (wrapper.getCommand() != PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) break achievement;

                inventory = true;
            }

            if (inventory) {
                long now = System.currentTimeMillis();

                boolean lagging = now - lastFlying > 60L;
                boolean attacking = playerData.getActionManager().getAttacking().get();
                boolean swinging = playerData.getActionManager().getSwinging().get();

                if (!lagging && (attacking || swinging)) {
                    if (++buffer > 2) {
                        fail();
                    }
                } else {
                    buffer = 0;
                }
            }
        } else if (object instanceof WrappedPlayInFlying) {
            lastFlying = System.currentTimeMillis();
            inventory = false;
        }
    }
}
