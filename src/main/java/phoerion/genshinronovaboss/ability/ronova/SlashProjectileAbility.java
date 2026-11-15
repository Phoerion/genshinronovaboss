package phoerion.genshinronovaboss.ability.ronova;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import phoerion.genshinronovaboss.ability.Ability;
import phoerion.genshinronovaboss.entity.custom.RonovaSlashProjectile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlashProjectileAbility extends Ability {

    private static final Map<UUID, Integer> PARALYZED_PLAYERS = new HashMap<>();

    public SlashProjectileAbility(String id, int cooldown) {
        super(id, cooldown);
    }

    public static void chargingTick(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            paralyze(serverPlayer, 2);
        }
    }

    public static void shoot(ServerPlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }

        Vec3d lookDir = player.getRotationVector().normalize();

        RonovaSlashProjectile projectile =
                new RonovaSlashProjectile(world, player);

        projectile.setVelocity(
                lookDir.x,
                lookDir.y,
                lookDir.z,
                1.0f,
                0.0f
        );

        projectile.setPosition(player.getX(), player.getEyeY(), player.getZ());

        world.spawnEntity(projectile);
    }

    @Override
    public void activate(ServerPlayerEntity player) {
        shoot(player);
    }

    public static void paralyze(ServerPlayerEntity player, int ticks) {
        PARALYZED_PLAYERS.put(player.getUuid(), ticks);
    }

    public static boolean isParalyzed(PlayerEntity player) {
        return PARALYZED_PLAYERS.containsKey(player.getUuid());
    }

    public static void tickParalysis(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        Integer ticks = PARALYZED_PLAYERS.get(uuid);
        if (ticks == null) return;

        if (ticks <= 1) {
            PARALYZED_PLAYERS.remove(uuid);
            return;
        }

        PARALYZED_PLAYERS.put(uuid, ticks - 1);

        player.setVelocity(0, 0, 0);
        player.velocityModified = true;
    }
}