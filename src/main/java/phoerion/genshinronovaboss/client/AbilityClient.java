package phoerion.genshinronovaboss.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import phoerion.genshinronovaboss.network.ModNetworking;

public class AbilityClient {
    private static KeyBinding abilityKey;
    private static KeyBinding secondabilityKey;
    private static KeyBinding thirdabilityKey;
    private static boolean wasPressed = false;

    public static void registerAbilities() {
        abilityKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.genshinronovaboss.ability",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_Z,
                        "category.genshinronovaboss"
                )
        );

        secondabilityKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.genshinronovaboss.second_ability",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        "category.genshinronovaboss"
                )
        );

        thirdabilityKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.genshinronovaboss.third_ability",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "category.genshinronovaboss"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                wasPressed = false;
                return;
            }

            boolean pressed = abilityKey.isPressed();

            if (pressed && !wasPressed) {
                ClientPlayNetworking.send(
                        ModNetworking.START_CHARGING_PACKET_ID,
                        PacketByteBufs.empty()
                );
            }

            if (!pressed && wasPressed) {
                ClientPlayNetworking.send(
                        ModNetworking.RELEASE_CHARGING_PACKET_ID,
                        PacketByteBufs.empty()
                );
            }

            wasPressed = pressed;

            if(secondabilityKey.wasPressed()){
                ClientPlayNetworking.send(
                        ModNetworking.SECOND_ABILITY_PACKET_ID,
                        PacketByteBufs.empty()
                );
            }

            if (thirdabilityKey.isPressed()) {
                ClientPlayNetworking.send(
                        ModNetworking.THIRD_ABILITY_PACKET_ID,
                        PacketByteBufs.empty()
                );
            }
        });
    }
}
