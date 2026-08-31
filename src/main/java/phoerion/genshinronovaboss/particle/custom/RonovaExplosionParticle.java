package phoerion.genshinronovaboss.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.random.Random; // Убедитесь, что этот импорт есть

public class RonovaExplosionParticle extends SpriteBillboardParticle {

    // Конструктор вашей частицы
    protected RonovaExplosionParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
                                      double xSpeed, double ySpeed, double zSpeed,
                                      SpriteProvider spriteSet) {
        super(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);

        this.setSprite(spriteSet.getSprite(this.random)); // Используем переданный провайдер для установки спрайта

        this.scale = 4.0f;
        this.maxAge = 40;
        this.gravityStrength = 0.0f;
        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();
        // Плавное исчезновение
        if (this.age < this.maxAge) {
            this.alpha = 1.0f - ((float) this.age / (float) this.maxAge);
            this.scale = 4.0f + this.age * 0.05f;
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
            return new RonovaExplosionParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteProvider);
        }
    }
}