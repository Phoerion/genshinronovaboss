package phoerion.genshinronovaboss;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.entity.client.RonovaProjectileRenderer;
import phoerion.genshinronovaboss.entity.client.RonovaRenderer;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.network.ModNetworking;
import phoerion.genshinronovaboss.particle.ModParticles;
import phoerion.genshinronovaboss.particle.custom.*;

public class GenshinRonovaBossClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.RONOVA_BOSS, RonovaRenderer::new);

        EntityRendererRegistry.register(ModEntities.PROJECTILE, RonovaProjectileRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.RONOVA_PARTICLE, RonovaExplosionParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RONOVA_CHARGE_PARTICLE, RonovaChargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RONOVA_TRAIL_PARTICLE, RonovaTrailParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RONOVA_EYE_PARTICLE, RonovaEyeParticle.Factory::new);


        final KeyBinding firstVisionAbilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.genshinronovaboss.ability",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "genshin"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (firstVisionAbilityKey.wasPressed()) {
                if (client.player != null) {
                    // Отправляем пакет на сервер
                    ClientPlayNetworking.send(ModNetworking.FIRST_VISION_ABILITY_PACKET_ID, PacketByteBufs.create());
                }
            }
        });
    }
}
