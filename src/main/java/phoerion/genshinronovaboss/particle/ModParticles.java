package phoerion.genshinronovaboss.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;

public class ModParticles {
    public static final DefaultParticleType RONOVA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType RONOVA_CHARGE_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType RONOVA_TRAIL_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType RONOVA_EYE_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles(){
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GenshinRonovaBoss.MOD_ID, "ronova_explosion_particle"), RONOVA_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GenshinRonovaBoss.MOD_ID, "ronova_charge_particle"), RONOVA_CHARGE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GenshinRonovaBoss.MOD_ID, "ronova_trail_particle"), RONOVA_TRAIL_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GenshinRonovaBoss.MOD_ID, "ronova_eye_particle"), RONOVA_EYE_PARTICLE);
    }
}
