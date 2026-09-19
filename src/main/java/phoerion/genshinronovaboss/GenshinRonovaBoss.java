package phoerion.genshinronovaboss;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phoerion.genshinronovaboss.client.ClientBossData;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.entity.custom.RonovaEyeEntity;
import phoerion.genshinronovaboss.item.ModItemGroups;
import phoerion.genshinronovaboss.item.ModItems;
import phoerion.genshinronovaboss.network.ModNetworking;
import phoerion.genshinronovaboss.particle.ModParticles;
import phoerion.genshinronovaboss.worldgen.ModWorldGen;
import phoerion.genshinronovaboss.worldgen.structures.CelestiaCommand;


public class GenshinRonovaBoss implements ModInitializer {
	public static final String MOD_ID = "genshinronovaboss";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


		ModWorldGen.register();

		ModItems.registerItems();
		ModItemGroups.registerItemGroups();
		ModNetworking.registerC2SPackets();

		ModEntities.registerEntities();

		ModParticles.registerParticles();

		ModEffects.registerModEffects();

		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) -> {
					CelestiaCommand.register(dispatcher);
				}
		);

		//DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, new RonovaSkyRenderer());


		FabricDefaultAttributeRegistry.register(ModEntities.RONOVA_BOSS, RonovaEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.RONOVA_EYE_ENTITY, RonovaEyeEntity.createMobAttributes());

		ClientPlayNetworking.registerGlobalReceiver(RonovaEntity.BOSS_STATE_PACKET_ID, (client, handler, buf, responseSender) -> {
			boolean isActive = buf.readBoolean();
			client.execute(() -> {
				ClientBossData.isRonovaFightActive = isActive;
			});
		});
	}
}
