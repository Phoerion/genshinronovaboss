package phoerion.genshinronovaboss.ability.ronova;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.ability.ChargedAbility;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import phoerion.genshinronovaboss.particle.ModParticles;
import phoerion.genshinronovaboss.screen.abilities.ronova.RonovaAbilityHud;

public class ExplodingProjectileAbility extends ChargedAbility {

    public ExplodingProjectileAbility(String id, int cooldown) {
        super(id, cooldown, 40);
    }

    @Override
    public void startCharging(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(player.getId());

        ServerPlayNetworking.send(player, new Identifier("spawn_player_particle"), buf);
    }

    @Override
    public void chargingTick(ServerPlayerEntity player, int chargeTime) {

        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }
    }

    @Override
    public void release(ServerPlayerEntity player, int chargeTime) {
        shoot(player, chargeTime);
    }

    private void shoot(ServerPlayerEntity player, int chargeTime){
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }

        int actualCharge = Math.min(
                Math.max(chargeTime, 0),
                getMaxChargeTime()
        );

        if (actualCharge < 5) {
            return;
        }

        Vec3d lookDir = player.getRotationVector().normalize();

        double charge = (double) actualCharge / getMaxChargeTime();
        double speed = 0.3 + charge * 1.5;

        RonovaProjectileEntity projectile =
                new RonovaProjectileEntity(world, player);

        projectile.setVelocity(
                lookDir.x * speed,
                lookDir.y * speed,
                lookDir.z * speed,
                1.0f,
                0.0f
        );

        world.spawnEntity(projectile);

    }


    @Override
    public void activate(ServerPlayerEntity player) {
    }
}