package phoerion.genshinronovaboss.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.PacketByteBuf;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RonovaTagSync {

    public static final Identifier CHANNEL =
            new Identifier(GenshinRonovaBoss.MOD_ID, "ronova_state");

    private static final Map<UUID, Boolean> PLAYER_STATES = new HashMap<>();

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                boolean hasTag = player.getCommandTags().contains("ronova");

                Boolean previousState = PLAYER_STATES.get(player.getUuid());

                if (previousState == null || previousState != hasTag) {

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(hasTag);

                    ServerPlayNetworking.send(player, CHANNEL, buf);

                    PLAYER_STATES.put(player.getUuid(), hasTag);
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PLAYER_STATES.remove(handler.player.getUuid());
        });
    }
}