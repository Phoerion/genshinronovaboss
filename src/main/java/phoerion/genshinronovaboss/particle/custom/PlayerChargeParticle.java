package phoerion.genshinronovaboss.particle.custom;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import phoerion.genshinronovaboss.ability.ronova.ExplodingAbilityManager;


public class PlayerChargeParticle extends SpriteBillboardParticle {
    int playerID;

    protected PlayerChargeParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, int playerID) {
        super(clientWorld, d, e, f);

        this.setSprite(spriteProvider);
        this.playerID = playerID;
        this.scale(0.0f);
        this.maxAge = 40;
        this.gravityStrength = 0.0f;
    }

    @Override
    public void tick() {
        super.tick();

        PlayerEntity player = (PlayerEntity) world.getEntityById(playerID);
        if(!(player == null)) {
            Vec3d look = player.getRotationVector();

            this.setPos(
                    player.getX() + look.x * 1.2,
                    player.getEyeY() - 0.2 + look.y * 1.2,
                    player.getZ() + look.z * 1.2
            );
        }

        if(!ExplodingAbilityManager.isCharging((ClientPlayerEntity) player)){
            this.markDead();
        }

        this.scale = 0.03f * age;

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
        public Particle createParticle(
                DefaultParticleType type,
                ClientWorld world,
                double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed
        ) {
            int playerId = (int) ySpeed;

            return new PlayerChargeParticle(
                    world, x, y, z,
                    spriteProvider,
                    playerId
            );
        }
    }
}

