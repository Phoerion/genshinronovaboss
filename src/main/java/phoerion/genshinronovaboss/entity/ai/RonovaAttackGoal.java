package phoerion.genshinronovaboss.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.core.jmx.Server;
import phoerion.genshinronovaboss.entity.custom.RonovaBurstProjectile;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import phoerion.genshinronovaboss.particle.ModParticles;

import java.util.EnumSet;

public class RonovaAttackGoal extends Goal {
    private final RonovaEntity mob;
    private int cooldown;
    private int tpCooldown;
    private int timeTillTp;
    private int timeTillBurst;
    private int keepShootingTime;
    private boolean isAttacking;
    private boolean isTpAttacking; // Отдельный флаг для телепортации, чтобы не ломать обычную стрельбу

    public RonovaAttackGoal(RonovaEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.cooldown = 80;
        this.tpCooldown = 400; // Интервал между телепортациями
        this.timeTillTp = 0;
        this.timeTillBurst = 0;
        this.keepShootingTime = 0;
        this.isAttacking = false;
        this.isTpAttacking = false;
    }

    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.isAttacking = false;
        this.isTpAttacking = false;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);
        double distanceSquared = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        // 1. Обычная стрельба шарами (если не занят телепортацией)
        if (!this.isTpAttacking) {
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            if (this.cooldown <= 40 && distanceSquared <= 4096.0) {
                this.shoot(target);
            }
        }

        // 2. Логика таймера телепортации
        if (!this.isTpAttacking) {
            if (this.tpCooldown > 0) {
                this.tpCooldown--;
            } else {
                // Время запускать ТП-атаку
                this.isTpAttacking = true;
                this.tpCooldown = 400;
                this.timeTillTp = 10;   // Через 2 тика телепортируемся
                this.timeTillBurst = 25; // Через 6 тиков начинаем стрелять очередью
                this.mob.triggerTPAttackAnimation();

            }

            // Экстренный триггер ТП, если игрок убежал дальше 32 блоков
            float distance = this.mob.distanceTo(target);
            if (distance >= 32.0F) {
                this.isTpAttacking = true;
                this.tpCooldown = 400;
                this.timeTillTp = 10;
                this.timeTillBurst = 25;
                this.mob.triggerTPAttackAnimation();
                if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            phoerion.genshinronovaboss.particle.ModParticles.RONOVA_EYE_PARTICLE,
                            this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                            1,
                            0.05, 0.05, 0.05,
                            0.0
                    );
                }
            }
        }

        // 3. Отсчет времени до телепортации
        if (this.timeTillTp > 0) {
            this.timeTillTp--;
            if (this.timeTillTp == 0) {
                tpRonova();
            }
        }

        // 4. Отсчет времени до начала серии выстрелов после ТП
        if (this.timeTillBurst > 0) {
            this.timeTillBurst--;
            if (this.timeTillBurst == 0) {
                this.keepShootingTime = 20; // Серия длится 20 тиков (1 секунду)
            }
        }

        // 5. Сама серия выстрелов (берст)
        if (this.keepShootingTime > 0) {
            this.keepShootingTime--;
            burst();
            if (this.keepShootingTime == 0) {
                this.isTpAttacking = false; // Возвращаем босса к обычному поведению
            }
        }
    }

    private void burst() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        // Спавним по 1 снаряду каждый тик в течение серии (всего 20 штук за секунду)
        RonovaBurstProjectile burstProjectile = new RonovaBurstProjectile(this.mob.getWorld(), this.mob);
        burstProjectile.setPosition(this.mob.getX(), this.mob.getEyeY() - 0.2, this.mob.getZ());

        double dX = target.getX() - this.mob.getX();
        double dY = target.getEyeY() - burstProjectile.getY();
        double dZ = target.getZ() - this.mob.getZ();

        burstProjectile.setVelocity(dX, dY, dZ, 2.0F, 10.0f);
        this.mob.getWorld().spawnEntity(burstProjectile);
    }

    private void tpRonova() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        Vec3d eyePosition = target.getEyePos();
        Vec3d lookVector = target.getRotationVector();

        // Правильное относительное смещение по всем осям X, Y, Z
        double targetX = eyePosition.x + lookVector.x * 6.0;
        double targetY = eyePosition.y + lookVector.y * 6.0;
        double targetZ = eyePosition.z + lookVector.z * 6.0;

        net.minecraft.util.math.BlockPos targetPos = new net.minecraft.util.math.BlockPos((int) targetX, (int) targetY, (int) targetZ);
        net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) this.mob.getWorld();

        // 2. Проверка: если точка находится внутри твердого блока или под землей, поднимаем босса на уровень ног/земли игрока
        if (!serverWorld.isAir(targetPos) || !serverWorld.isAir(targetPos.up())) {
            // Устанавливаем высоту ровно на уровень ног игрока, чтобы избежать застревания в стенах
            targetY = target.getY();
        }

        this.mob.setPosition(targetX, targetY, targetZ);
        serverWorld.spawnParticles(ModParticles.RONOVA_EYE_PARTICLE,
                targetX, targetY, targetZ,
                1,
                0.0f, 0.0f, 0.0f,
                0.0f);
        this.mob.getLookControl().lookAt(target, 360.0F, 360.0F);
        this.mob.setYaw(target.getYaw() + 180.0F); // Лицом к игроку
    }

    private void shoot(LivingEntity target) {
        if (this.cooldown == 40) {
            if (!this.isAttacking) {
                spawnChargeParticle();
                this.mob.triggerAttackAnimation();
                this.isAttacking = true;
            }
        }

        if (this.cooldown == 0) {
            this.cooldown = 120;
            RonovaProjectileEntity projectile = new RonovaProjectileEntity(this.mob.getWorld(), this.mob);
            projectile.setPosition(this.mob.getX(), this.mob.getEyeY() - 0.2, this.mob.getZ());

            double dX = target.getX() - this.mob.getX();
            double dY = target.getEyeY() - projectile.getY();
            double dZ = target.getZ() - this.mob.getZ();

            projectile.setVelocity(dX, dY, dZ, 2.0F, 0.0001f);
            this.mob.getWorld().spawnEntity(projectile);
            this.isAttacking = false;
        }
    }

    private void spawnChargeParticle() {
        float headYaw = this.mob.getHeadYaw() * ((float) Math.PI / 180F);
        float pitch = this.mob.getPitch() * ((float) Math.PI / 180F);

        double lookX = -Math.sin(headYaw) * Math.cos(pitch);
        double lookZ = Math.cos(headYaw) * Math.cos(pitch);

        double particleX = this.mob.getX() + lookX * 1.0;
        double particleY = this.mob.getY() + this.mob.getHeight() * 0.5;
        double particleZ = this.mob.getZ() + lookZ * 1.0;

        if (!this.mob.getWorld().isClient()) {
            net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) this.mob.getWorld();

            serverWorld.spawnParticles(
                    ModParticles.RONOVA_CHARGE_PARTICLE,
                    particleX, particleY, particleZ,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }
    }
}