package phoerion.genshinronovaboss.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class CurseParticle extends SpriteBillboardParticle {
    protected CurseParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);

        this.setSprite(spriteProvider);

        this.scale(1.0f);
        this.maxAge = 20;
        this.gravityStrength = 0.0f;
        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();

        this.alpha = 1.0f - (float) age / maxAge;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new CurseParticle(level, x, y, z, this.spriteProvider);
        }
    }
}


