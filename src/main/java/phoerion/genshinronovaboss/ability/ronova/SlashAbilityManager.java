package phoerion.genshinronovaboss.ability.ronova;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import phoerion.genshinronovaboss.network.ModNetworking;
import phoerion.genshinronovaboss.screen.abilities.ronova.RonovaAbilityHud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlashAbilityManager {

    private static final int COOLDOWN_TICKS = 500;
    private static final int CHARGE_TICKS = 60;

    private static final Map<UUID, Integer> COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> CHARGETIME = new HashMap<>();
    private static final Map<UUID, Boolean> CHARGING = new HashMap<>();

    public static final SlashProjectileAbility SLASH_PROJECTILE =
            new SlashProjectileAbility(
                    "slash_projectile_ability",
                    COOLDOWN_TICKS
            );

    public static void activate(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        if (!player.getCommandTags().contains("ronova")) return;
        if (CHARGING.getOrDefault(uuid, false)) return;
        if (COOLDOWNS.getOrDefault(uuid, 0) > 0) return;

        CHARGETIME.put(uuid, 0);
        CHARGING.put(uuid, true);

    }

    public static void tick(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        int cooldown = COOLDOWNS.getOrDefault(uuid, 0);
        ModNetworking.syncSlashCooldown(player, cooldown);

        if (cooldown > 0) {
            COOLDOWNS.put(uuid, cooldown - 1);
        }

        if (!CHARGING.getOrDefault(uuid, false)) return;

        int chargeTime = CHARGETIME.getOrDefault(uuid, 0);

        SlashProjectileAbility.chargingTick(player);

        chargeTime++;
        CHARGETIME.put(uuid, chargeTime);

        if (chargeTime >= CHARGE_TICKS) {
            CHARGETIME.put(uuid, 0);
            CHARGING.put(uuid, false);

            SlashProjectileAbility.shoot(player);

            COOLDOWNS.put(uuid, COOLDOWN_TICKS);
        }
    }

    public static int getChargeTime(ServerPlayerEntity player) {
        return CHARGETIME.getOrDefault(player.getUuid(), 0);
    }

    public static int getCooldown(ServerPlayerEntity player) {
        return COOLDOWNS.getOrDefault(player.getUuid(), 0);
    }

    public static int getMaxCooldown(){
        return COOLDOWN_TICKS;
    }
}