package phoerion.genshinronovaboss.network;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import phoerion.genshinronovaboss.particle.ModParticles;
import phoerion.genshinronovaboss.screen.abilities.ronova.RonovaAbilityHud;

public class ModByteBufs {

    public static void registerByteBufs(){
        //player charge ability buf (sends player to the paricle)
        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("spawn_player_particle"),
                (client, handler, buf, responseSender) -> {

                    int playerId = buf.readVarInt();

                    client.execute(() -> {
                        if (client.world == null) return;

                        Entity entity = client.world.getEntityById(playerId);

                        if (!(entity instanceof PlayerEntity player)) return;

                        Vec3d look = player.getRotationVector();

                        double x = player.getX() + look.x * 1.2;
                        double y = player.getEyeY() - 0.2 + look.y * 1.2;
                        double z = player.getZ() + look.z * 1.2;

                        client.world.addParticle(
                                ModParticles.PLAYER_CHARGE_PARTICLE,
                                x, y, z,
                                0,
                                playerId,
                                0
                        );
                    });
                }
        );

        //ronova tag buff
        ClientPlayNetworking.registerGlobalReceiver(
                RonovaTagSync.CHANNEL,
                (client, handler, buf, responseSender) -> {
                    boolean hasTag = buf.readBoolean();

                    client.execute(() ->
                            RonovaAbilityHud.setRonovaTag(hasTag)
                    );
                }
        );

        // First ability cooldown
        ClientPlayNetworking.registerGlobalReceiver(
                ModNetworking.COOLDOWN_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    int value = buf.readInt();

                    client.execute(() ->
                            RonovaAbilityHud.setCooldown(value)
                    );
                }
        );

// Curse ability cooldown
        ClientPlayNetworking.registerGlobalReceiver(
                ModNetworking.CURSE_COOLDOWN_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    int value = buf.readInt();

                    client.execute(() ->
                            RonovaAbilityHud.setCurseCooldown(value)
                    );
                }
        );

// Slash ability cooldown
        ClientPlayNetworking.registerGlobalReceiver(
                ModNetworking.SLASH_COOLDOWN_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    int value = buf.readInt();

                    client.execute(() ->
                            RonovaAbilityHud.setSlashCooldown(value)
                    );
                }
        );

        // Can Curse
        ClientPlayNetworking.registerGlobalReceiver(
                ModNetworking.CAN_CURSE_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    boolean value = buf.readBoolean();

                    client.execute(() ->
                            RonovaAbilityHud.setCanCurse(value)
                    );
                }
        );
    }

}



