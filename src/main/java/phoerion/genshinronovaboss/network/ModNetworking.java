package phoerion.genshinronovaboss.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.ability.ronova.CurseOnDamageAbility;
import phoerion.genshinronovaboss.ability.ronova.CurseOnDamageAbilityManager;
import phoerion.genshinronovaboss.ability.ronova.ExplodingAbilityManager;
import phoerion.genshinronovaboss.ability.ronova.SlashAbilityManager;
import phoerion.genshinronovaboss.item.ModItems;
import phoerion.genshinronovaboss.item.custom.StellarVisionItem;

public class ModNetworking {

    public static final Identifier FIRST_ABILITY_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "first_ability");

    public static final Identifier START_CHARGING_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "start_charging");

    public static final Identifier RELEASE_CHARGING_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "release_charging");

    public static final Identifier SECOND_ABILITY_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "second_ability");
    public static final Identifier THIRD_ABILITY_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "third_ability");

    public static final Identifier COOLDOWN_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "cooldown");

    public static final Identifier CURSE_COOLDOWN_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "curse_cooldown");

    public static final Identifier SLASH_COOLDOWN_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "slash_cooldown");

    public static final Identifier CAN_CURSE_PACKET_ID =
            new Identifier(GenshinRonovaBoss.MOD_ID, "can_curse");

    public static void syncCooldown(ServerPlayerEntity player, int value) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(value);
        ServerPlayNetworking.send(player, COOLDOWN_PACKET_ID, buf);
    }

    public static void syncCurseCooldown(ServerPlayerEntity player, int value) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(value);
        ServerPlayNetworking.send(player, CURSE_COOLDOWN_PACKET_ID, buf);
    }

    public static void syncSlashCooldown(ServerPlayerEntity player, int value) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(value);
        ServerPlayNetworking.send(player, SLASH_COOLDOWN_PACKET_ID, buf);
    }

    public static void syncCanCurse(ServerPlayerEntity player, boolean value) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(value);
        ServerPlayNetworking.send(player, CAN_CURSE_PACKET_ID, buf);
    }


    public static void registerC2SPackets() {

        ServerPlayNetworking.registerGlobalReceiver(
                FIRST_ABILITY_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {

                    server.execute(() -> {

                        if (player.getCommandTags().contains("ronova")) {

                        } else if (player.getInventory().contains(
                                new ItemStack(ModItems.STELLAR_VISION))) {

                            StellarVisionItem.giveIceSword(
                                    player.getWorld(),
                                    player
                            );
                        }
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                START_CHARGING_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {

                    server.execute(() -> {
                        ExplodingAbilityManager.startCharging(player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                RELEASE_CHARGING_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {

                    server.execute(() -> {
                        ExplodingAbilityManager.release(player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                SECOND_ABILITY_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {

                    server.execute(() -> {
                        CurseOnDamageAbilityManager.activate(player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                THIRD_ABILITY_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        SlashAbilityManager.activate(player);
                    });
                }
        );
    }
}

