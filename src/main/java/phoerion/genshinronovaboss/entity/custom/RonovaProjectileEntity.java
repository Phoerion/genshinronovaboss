package phoerion.genshinronovaboss.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.item.ModItems;
import phoerion.genshinronovaboss.particle.ModParticles;
import phoerion.genshinronovaboss.util.RonovaExplosionUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RonovaProjectileEntity extends PersistentProjectileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int age;

    public RonovaProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.PROJECTILE, owner, world);
    }

    public RonovaProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ItemStack asItemStack() {
        return ModItems.STELLAR_VISION.getDefaultStack();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        //controllers.add(new AnimationController<>(this, "controller", state ->
        //        state.setAndContinue(RawAnimation.begin().thenLoop("fly"))
        //));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.getWorld().isClient() && entityHitResult.getEntity() != this.getOwner()) {
            explode();
            if (!this.getWorld().isClient) {
                this.getWorld().addParticle(
                        ModParticles.RONOVA_PARTICLE,
                        this.getX(), this.getY(), this.getZ(),
                        0.0f, 0.0f, 0.0f
                );
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient()) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = this.getWorld().getBlockState(pos);

                explode();

            }
        }
    }

    private void explode() {
        World world = this.getWorld();

        if (!world.isClient()) {
            // 1. Создаем ванильный взрыв и наносим урон
            RonovaExplosionUtil.createShieldBreakingExplosion((ServerWorld) this.getWorld(), this.getOwner(), this.getPos());

            // 2. Сначала отправляем сигнал клиенту, пока сущность еще существует
            world.sendEntityStatus(this, (byte) 3);

            // 3. Только после отправки удаляем снаряд
            this.discard();
        }
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        // Получаем сигнал на клиенте и спавним партиклы
        if (status == 3 && this.getWorld().isClient()) {
            for (int i = 0; i < 30; ++i) {
                double offsetX = this.random.nextDouble() * 2.0 - 1.0;
                double offsetY = this.random.nextDouble() * 2.0 - 1.0;
                double offsetZ = this.random.nextDouble() * 2.0 - 1.0;

                this.getWorld().addParticle(
                        ModParticles.RONOVA_PARTICLE,
                        this.getX(), this.getY(), this.getZ(),
                        offsetX * 0.2, offsetY * 0.2, offsetZ * 0.2
                );
            }
        }
    }

    @Override
    public void tick() {
        Vec3d currentVelocity = this.getVelocity();

        super.tick();

        if (this.isAlive()) {
            this.setVelocity(currentVelocity);
        }

        explodeOnHit();
        this.setNoGravity(true);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ModParticles.RONOVA_TRAIL_PARTICLE,
                    this.getX(), this.getY(), this.getZ(),
                    1,
                    0.05, 0.05, 0.05,
                    0.0
            );
        }

        age++;
        if (age >= 200) {
            this.explode();
        }
    }

    private void explodeOnHit() {
        if (!this.getWorld().isClient()) {
            // Проверяем область вокруг снаряда (например, радиус 1 блок)
            Box checkArea = this.getBoundingBox().expand(0.5D);

            // Ищем чужие стрелы или снаряды
            java.util.List<PersistentProjectileEntity> incomingProjectiles =
                    this.getWorld().getEntitiesByClass(
                            PersistentProjectileEntity.class,
                            checkArea,
                            projectile -> projectile != this && projectile.isAlive()
                    );

            for (PersistentProjectileEntity projectile : incomingProjectiles) {
                // Вызываем вашу логику взрыва/столкновения
                this.explode();

                // Уничтожаем чужую стрелу
                projectile.discard();
                break;
            }
        }
    }

    @Override
    public boolean isInsideWaterOrBubbleColumn() {
        return false;
    }

    @Override
    public boolean isInLava() {
        return false;
    }

    @Override
    protected float getDragInWater() {
        return 1.0f;
    }

    @Override
    protected void onHit(LivingEntity target) {
        this.explode();
    }


}