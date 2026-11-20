package phoerion.genshinronovaboss.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.particle.ModParticles;

public class RonovaExplosionUtil {

    public static void createShieldBreakingExplosion(ServerWorld world, Entity sourceEntity, Vec3d pos) {
        float power = 0.0f;
        float damage = 30.0f;
        float box = 5.0f;
        int radius = (int) Math.ceil(power);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    net.minecraft.util.math.BlockPos targetPos = new net.minecraft.util.math.BlockPos(
                            (int)pos.x + x, (int)pos.y + y, (int)pos.z + z
                    );

                    if (targetPos.getSquaredDistance(pos.x, pos.y, pos.z) <= power * power) {
                        if (!world.getBlockState(targetPos).isAir() && !world.getBlockState(targetPos).isOf(net.minecraft.block.Blocks.BEDROCK)) {
                            world.breakBlock(targetPos, true, sourceEntity);
                        }
                    }
                }
            }
        }

        world.playSound(
                null, pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE,
                5.0F,
                0.6F
        );

        world.spawnParticles(
                phoerion.genshinronovaboss.particle.ModParticles.RONOVA_TRAIL_PARTICLE,
                pos.x, pos.y, pos.z,
                1,
                0.05, 0.05, 0.05,
                0.0
        );

        Box damageBox = new Box(
                pos.x - box, pos.y - box, pos.z - box,
                pos.x + box, pos.y + box, pos.z + box
        );

        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, damageBox, e -> e != sourceEntity && e.isAlive())) {
            double distanceSq = entity.squaredDistanceTo(pos);
            if (distanceSq <= box * box) {
                double knockBack = 1.8;

                if (entity instanceof PlayerEntity player && player.isUsingItem() && player.getActiveItem().isOf(Items.SHIELD)) {
                    player.disableShield(true);
                    knockBack = 0.0;
                    damage = damage / 2;
                }

                entity.damage(world.getDamageSources().explosion(null, sourceEntity), damage);

                Random random = world.getRandom();
                if(random.nextFloat() <= 0.3f){
                    entity.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 200, 0, false, true));
                }

                double dX = entity.getX() - pos.x;
                double dY = entity.getEyeY() - pos.y;
                double dZ = entity.getZ() - pos.z;
                double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

                if (distance > 0.0) {
                    entity.addVelocity(
                            (dX / distance) * knockBack,
                            1.0,
                            (dZ / distance) * knockBack
                    );
                    entity.velocityModified = true;
                }
            }
        }
    }
}