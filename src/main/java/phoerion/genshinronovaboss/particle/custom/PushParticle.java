package phoerion.genshinronovaboss.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class PushParticle extends SpriteBillboardParticle {
    protected PushParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);

        this.setSprite(spriteProvider);

        this.scale(0.0f);
        this.maxAge = 10;
        this.gravityStrength = 0.0f;
        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();

        this.scale = 0.0f + 1.0f * age;
        this.alpha = 1.0f - (float) age / maxAge;
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
            return new PushParticle(level, x, y, z, this.spriteProvider);
        }
    }
}


