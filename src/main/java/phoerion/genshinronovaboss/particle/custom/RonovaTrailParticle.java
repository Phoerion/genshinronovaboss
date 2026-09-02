package phoerion.genshinronovaboss.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class RonovaTrailParticle extends SpriteBillboardParticle {
    protected RonovaTrailParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.scale = 1.5f;
        this.maxAge = 20;
        this.gravityStrength = 0.0f;
        this.alpha = 1.0f;
        this.setSprite(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - ((float) this.age/(float) this.maxAge);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    // --- ФАБРИКА ---
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        // ПОЛЕ ФАБРИКИ
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            // Инициализация поля класса переданным аргументом
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new RonovaTrailParticle(level, x, y, z, this.spriteProvider);
        }
    }
}
