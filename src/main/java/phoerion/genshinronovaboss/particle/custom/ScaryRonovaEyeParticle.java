package phoerion.genshinronovaboss.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ScaryRonovaEyeParticle extends SpriteBillboardParticle {
    protected ScaryRonovaEyeParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);

        this.setSprite(spriteProvider);

        this.maxAge = 80;
        this.gravityStrength = 0.0f;
        this.angle = 0.593f;
        this.prevAngle = this.angle;

    }

    @Override
    public void tick() {
        super.tick();
        if (this.scale <= 6.0){
            this.scale = (float) (this.age * 0.2);
        }
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
            return new ScaryRonovaEyeParticle(level, x, y, z, this.spriteProvider);
        }
    }
}
