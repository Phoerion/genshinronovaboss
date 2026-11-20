package phoerion.genshinronovaboss.ability.ronova;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import phoerion.genshinronovaboss.network.ModNetworking;
import phoerion.genshinronovaboss.screen.abilities.ronova.RonovaAbilityHud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExplodingAbilityManager {

    private static final int COOLDOWN_TICKS = 200;
    private static final int MAX_CHARGE_TICKS = 40;

    private static final Map<UUID, Integer> CHARGING =
            new HashMap<>();

    private static final Map<UUID, Integer> COOLDOWNS =
            new HashMap<>();

    public static final ExplodingProjectileAbility EXPLODING_PROJECTILE =
            new ExplodingProjectileAbility(
                    "exploding_projectile_ability",
                    COOLDOWN_TICKS
            );

    public static int getMaxCooldown(){
        return (int) COOLDOWN_TICKS;
    }

    public static void startCharging(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();

        if (!player.getCommandTags().contains("ronova")) {
            return;
        }

        if (CHARGING.containsKey(uuid)) {
            return;
        }

        if (COOLDOWNS.getOrDefault(uuid, 0) > 0) {
            return;
        }

        CHARGING.put(uuid, 0);

        EXPLODING_PROJECTILE.startCharging(player);
    }

    public static void tick(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();

        int cooldown = COOLDOWNS.getOrDefault(uuid, 0);
        ModNetworking.syncCooldown(player, cooldown);

        if (cooldown > 0) {
            COOLDOWNS.put(uuid, cooldown - 1);
        }

        if (!CHARGING.containsKey(uuid)) {
            return;
        }

        if (!player.getCommandTags().contains("ronova")) {
            CHARGING.remove(uuid);
            return;
        }

        int chargeTime = CHARGING.get(uuid);

        if (chargeTime < MAX_CHARGE_TICKS) {
            chargeTime++;
            CHARGING.put(uuid, chargeTime);
        }
        else{
            release(player);
        }

        EXPLODING_PROJECTILE.chargingTick(player, chargeTime);
    }

    public static void release(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();

        if (!CHARGING.containsKey(uuid)) {
            return;
        }

        int chargeTime = CHARGING.remove(uuid);

        if (!player.getCommandTags().contains("ronova")) {
            return;
        }

        if (chargeTime < 5) {
            return;
        }

        EXPLODING_PROJECTILE.release(player, chargeTime);

        COOLDOWNS.put(uuid, COOLDOWN_TICKS);
    }

    public static boolean isCharging(ServerPlayerEntity player) {
        return CHARGING.containsKey(player.getUuid());
    }
    public static boolean isCharging(ClientPlayerEntity player) {
        return CHARGING.containsKey(player.getUuid());
    }

    public static int getChargeTime(ServerPlayerEntity player) {
        return CHARGING.getOrDefault(player.getUuid(), 0);
    }

    public static int getCooldown(ServerPlayerEntity player) {
        return COOLDOWNS.getOrDefault(player.getUuid(), 0);
    }
}