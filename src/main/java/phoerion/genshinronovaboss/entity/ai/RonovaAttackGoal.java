package phoerion.genshinronovaboss.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.apache.logging.log4j.core.jmx.Server;
import phoerion.genshinronovaboss.entity.custom.*;
import phoerion.genshinronovaboss.particle.ModParticles;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class RonovaAttackGoal extends Goal {
    private Set<RonovaEyeEntity> EYES = new HashSet<>();
    private final RonovaEntity mob;
    private int cooldown;
    private int tpCooldown;
    private int timeTillTp;
    private int timeTillBurst;
    private int keepShootingTime;
    private int slashCooldown;
    private boolean isAttacking;
    private boolean isTpAttacking; // Отдельный флаг для телепортации, чтобы не ломать обычную стрельбу
    private boolean isSlashAttacking;
    private boolean isSummonning;
    private boolean areSummonsAlive;
    private int summonCooldown;

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
        this.cooldown = 120;
        this.tpCooldown = 300; // Интервал между телепортациями
        this.slashCooldown = 200;
        this.summonCooldown = 300;
        this.timeTillTp = 0;
        this.timeTillBurst = 0;
        this.keepShootingTime = 0;
        this.isAttacking = false;
        this.isTpAttacking = false;
        this.isSlashAttacking = false;
        this.isSummonning = false;
    }

    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.isAttacking = false;
        this.isTpAttacking = false;
        this.isSlashAttacking = false;
        this.isSummonning = false;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;
        if(this.mob.isTryingToEscape) return;

        this.EYES.clear(); // Важно очищать список каждый тик, чтобы старые/мертвые глаза удалялись
        for (RonovaEyeEntity eye : this.mob.getWorld().getEntitiesByClass(RonovaEyeEntity.class, this.mob.getBoundingBox().expand(128), e -> e.isAlive() && e.getRonovaBoss() != null)) {
            if(eye.getRonovaBoss().equals(this.mob))
                this.EYES.add(eye);
        }

        this.areSummonsAlive = !this.EYES.isEmpty();

        this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);
        double distanceSquared = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        if (!this.isTpAttacking && !this.isSlashAttacking && !this.isAttacking && !this.areSummonsAlive) {
            if (this.summonCooldown > 0) {
                this.summonCooldown--;
            }
            if (this.summonCooldown <= 40 && distanceSquared <= 1000.0) {
                this.summonEyes();
            }
        }
        // 1. Обычная стрельба шарами (если не занят телепортацией)
        if (!this.isTpAttacking && !this.isSlashAttacking && !this.isSummonning) {
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            if (this.cooldown <= 40 && distanceSquared <= 10000.0) {
                this.shoot(target);
            }
        }

        if(!this.isTpAttacking && !this.isAttacking && !this.isSummonning){
            if(this.slashCooldown > 0){
                this.slashCooldown --;
            }
            if (this.slashCooldown <= 25 && distanceSquared <= 10000.0) {
                this.slashShoot(target);
            }
        }

        // 2. Логика таймера телепортации
        if (!this.isTpAttacking && !this.isSlashAttacking && !this.isSummonning) {
            if (this.tpCooldown > 0) {
                this.tpCooldown--;
            } else {
                // Время запускать ТП-атаку
                this.isTpAttacking = true;
                this.tpCooldown = 300;
                this.timeTillTp = 10;   // Через 2 тика телепортируемся
                this.timeTillBurst = 25; // Через 6 тиков начинаем стрелять очередью
                this.mob.triggerTPAttackAnimation();

            }

            // Экстренный триггер ТП, если игрок убежал дальше 32 блоков
            float distance = this.mob.distanceTo(target);
            if (distance >= 32.0F) {
                this.isTpAttacking = true;
                this.tpCooldown = 300;
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

    private void summonEyes() {
        if(!this.mob.getWorld().isClient) {
            if (this.summonCooldown == 40) {
                this.isSummonning = true;
                this.mob.triggerSummonAttackAnimation();
            }
            if(this.summonCooldown == 0){
                for(int i = 0; i <=2; i++){
                    RonovaEyeEntity eyeEntity = new RonovaEyeEntity(this.mob.getWorld(), this.mob);
                    eyeEntity.setRonova(this.mob);
                    Random random = this.mob.getWorld().getRandom();
                    eyeEntity.setPosition(this.mob.getX() -20 + random.nextInt(41), this.mob.getEyeY(), this.mob.getZ() -20 + random.nextInt(41));

                    this.mob.getWorld().spawnEntity(eyeEntity);
                }
                this.summonCooldown = 300;
                this.isSummonning = false;
            }
        }

    }

    private void slashShoot(LivingEntity target) {
        if (this.slashCooldown == 25) {
            if (!this.isSlashAttacking) {
                this.mob.triggerSlashAttackAnimation();
                this.isSlashAttacking = true;
            }
        }

        if (this.slashCooldown == 0) {
            this.slashCooldown = 200;
            Vec3d look = this.mob.getRotationVector();
            RonovaSlashProjectile projectile = new RonovaSlashProjectile(this.mob.getWorld(), this.mob);
            projectile.setPosition(this.mob.getX() + look.x * 1.5, this.mob.getEyeY() - 5.2, this.mob.getZ() +  look.z * 1.5);

            double dX = target.getX() - this.mob.getX();
            double dY = target.getEyeY() - projectile.getY();
            double dZ = target.getZ() - this.mob.getZ();

            projectile.setVelocity(dX, dY, dZ, 1.0F, 0.0001f);
            this.mob.getWorld().spawnEntity(projectile);
            this.isSlashAttacking = false;
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