package phoerion.genshinronovaboss;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.item.ModItemGroups;
import phoerion.genshinronovaboss.item.ModItems;
import phoerion.genshinronovaboss.network.ModNetworking;
import phoerion.genshinronovaboss.particle.ModParticles;

public class GenshinRonovaBoss implements ModInitializer {
	public static final String MOD_ID = "genshinronovaboss";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.registerItems();
		ModItemGroups.registerItemGroups();
		ModNetworking.registerC2SPackets();

		ModEntities.registerEntities();

		ModParticles.registerParticles();


		FabricDefaultAttributeRegistry.register(ModEntities.RONOVA_BOSS, RonovaEntity.createMobAttributes());
	}
}
