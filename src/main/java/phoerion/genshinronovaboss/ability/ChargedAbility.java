package phoerion.genshinronovaboss.ability;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ChargedAbility extends Ability {

    private final int maxChargeTime;

    public ChargedAbility(String id, int cooldown, int maxChargeTime) {
        super(id, cooldown);
        this.maxChargeTime = maxChargeTime;
    }

    public int getMaxChargeTime() {
        return maxChargeTime;
    }

    public abstract void startCharging(ServerPlayerEntity player);

    public abstract void chargingTick(ServerPlayerEntity player, int chargeTime);

    public abstract void release(ServerPlayerEntity player, int chargeTime);

    @Override
    public void activate(ServerPlayerEntity player) {

    }
}