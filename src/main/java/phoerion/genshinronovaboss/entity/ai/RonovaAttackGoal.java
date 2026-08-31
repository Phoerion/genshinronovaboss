package phoerion.genshinronovaboss.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import phoerion.genshinronovaboss.particle.ModParticles;

import java.util.EnumSet;

public class RonovaAttackGoal extends Goal {
    private final RonovaEntity mob;
    private int cooldown;

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
    }

    @Override
    public void stop() {
        this.mob.setAttacking(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        // Поворачиваем моба лицом к цели
        this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);
        double distanceSquared = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

        // Уменьшаем кулдаун атаки каждый тик
        if (this.cooldown > 0) {
            this.cooldown--;
        }

        // Если кулдаун вышел и цель находится на расстоянии до 16 блоков — стреляем
        if (this.cooldown <= 40 && distanceSquared <= 1024.0) {
            this.shoot(target);
            // Время перезарядки в тиках (4 секунды)
        }
    }

    private void shoot(LivingEntity target) {
        if (this.cooldown == 40) {
            spawnChargeParticle();
        }

        if (this.cooldown == 0) {
            this.cooldown = 120;
            // Создаем снаряд
            RonovaProjectileEntity projectile = new RonovaProjectileEntity(this.mob.getWorld(), this.mob);

            // Вычисляем направление полета к цели
            double dX = target.getX() - this.mob.getX();
            double dY = target.getEyeY() - 1.1 - projectile.getY();
            double dZ = target.getZ() - this.mob.getZ();

            // Устанавливаем скорость снаряда (1.5F — скорость, 1.0F — разброс)
            projectile.setVelocity(dX, dY, dZ, 1.0F, 0.3F);

            // Спавним снаряд в мире
            this.mob.getWorld().spawnEntity(projectile);
        }
    }

    private void spawnChargeParticle() {
        // Получаем угол поворота головы в радианах
        float headYaw = this.mob.getHeadYaw() * ((float) Math.PI / 180F);
        float pitch = this.mob.getPitch() * ((float) Math.PI / 180F);

        // Вычисляем вектор взгляда по углу головы
        double lookX = -Math.sin(headYaw) * Math.cos(pitch);
        double lookZ = Math.cos(headYaw) * Math.cos(pitch);

        double particleX = this.mob.getX() + lookX * 1.0;
        double particleY = this.mob.getY() + this.mob.getHeight() * 0.5;
        double particleZ = this.mob.getZ() + lookZ * 1.0;

        if (!this.mob.getWorld().isClient()) {
            net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) this.mob.getWorld();

            serverWorld.spawnParticles(
                    phoerion.genshinronovaboss.particle.ModParticles.RONOVA_CHARGE_PARTICLE,
                    particleX, particleY, particleZ,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }

        this.mob.setAttacking(true);
    }


}

