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
        float box = 6.0f;
        int radius = (int) Math.ceil(power);

        // 1. Исправленный цикл по всем блокам в радиусе взрыва
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

        // 1. Звук взрыва босса
        world.playSound(
                null, pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE,
                5.0F,
                0.6F
        );

        // 2. Визуальный эффект частиц (масштабированный под силу 6.0)
        world.spawnParticles(
                phoerion.genshinronovaboss.particle.ModParticles.RONOVA_TRAIL_PARTICLE,
                pos.x, pos.y, pos.z, // Координаты центра снаряда
                1,                                           // Количество частиц за раз
                0.05, 0.05, 0.05,                            // Небольшой случайный разброс по осям
                0.0                                          // Скорость движения частиц
        );

        // 3. Зона поражения по силе взрыва
        Box damageBox = new Box(
                pos.x - box, pos.y - box, pos.z - box,
                pos.x + box, pos.y + box, pos.z + box
        );

        // 4. Обработка сущностей в радиусе
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, damageBox, e -> e != sourceEntity && e.isAlive())) {
            double distanceSq = entity.squaredDistanceTo(pos);
            if (distanceSq <= box * box) {

                // Проверка на игрока с поднятым щитом — пробитие и отключение щита
                if (entity instanceof PlayerEntity player && player.isUsingItem() && player.getActiveItem().isOf(Items.SHIELD)) {
                    player.disableShield(true); // Принудительно ломает анимацию блока и отключает щит на 5 секунд
                }

                // Нанесение урона
                entity.damage(world.getDamageSources().explosion(null, sourceEntity), damage);

                Random random = world.getRandom();
                if(random.nextFloat() <= 0.3f){
                    entity.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 200, 0, false, true));
                }

                // Мощный отброс от эпицентра взрыва (усилен под силу 6.0)
                double dX = entity.getX() - pos.x;
                double dY = entity.getEyeY() - pos.y;
                double dZ = entity.getZ() - pos.z;
                double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

                if (distance > 0.0) {
                    entity.addVelocity(
                            (dX / distance) * 1.8,
                            0.8,
                            (dZ / distance) * 1.8
                    );
                    entity.velocityModified = true;
                }
            }
        }
    }
}