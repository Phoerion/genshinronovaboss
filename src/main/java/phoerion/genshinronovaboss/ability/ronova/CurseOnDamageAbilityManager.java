package phoerion.genshinronovaboss.ability.ronova;

import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import phoerion.genshinronovaboss.network.ModNetworking;
import phoerion.genshinronovaboss.screen.abilities.ronova.RonovaAbilityHud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurseOnDamageAbilityManager {

    private static final int COOLDOWN_TICKS = 100;

    private static final Map<UUID, Integer> COOLDOWNS =
            new HashMap<>();

    public static final CurseOnDamageAbility CURSE_ON_DAMAGE_ABILITY =
            new CurseOnDamageAbility(
                    "exploding_projectile_ability",
                    COOLDOWN_TICKS
            );

    public static int getMaxCooldown(){
        return COOLDOWN_TICKS;
    }

    public static void activate(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();

        if (!player.getCommandTags().contains("ronova")) {
            return;
        }

        if (COOLDOWNS.getOrDefault(uuid, 0) > 0) {
            return;
        }

        CURSE_ON_DAMAGE_ABILITY.activate(player);
        COOLDOWNS.put(uuid, COOLDOWN_TICKS);
    }

    public static void tick(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();

        int cooldown = COOLDOWNS.getOrDefault(uuid, 0);

        if (cooldown > 0) {
            COOLDOWNS.put(uuid, cooldown - 1);
        }

        if (!player.getCommandTags().contains("ronova")) {
            return;
        }

        ModNetworking.syncCurseCooldown(player, cooldown);
        ModNetworking.syncCanCurse(player, player.getCommandTags().contains("canCurse"));
    }

    public static int getCooldown(ServerPlayerEntity player) {
        return COOLDOWNS.getOrDefault(player.getUuid(), 0);
    }
}