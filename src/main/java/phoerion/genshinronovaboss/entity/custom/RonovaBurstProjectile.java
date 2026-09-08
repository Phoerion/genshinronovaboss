package phoerion.genshinronovaboss.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.particle.custom.RonovaEyeParticle;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RonovaBurstProjectile extends PersistentProjectileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int age;

    public RonovaBurstProjectile(World world, LivingEntity owner) {
        super(ModEntities.PROJECTILE, owner, world);
    }

    public RonovaBurstProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();
        age++;
        if(age >= 20){
            this.discard();
        }

 //       if (!this.getWorld().isClient()) {
//            // Находим ближайшую живую сущность в радиусе 16 блоков (или используем конкретную цель)
//
//            LivingEntity target = null;
//            if(this.getOwner() instanceof RonovaEntity ronova){
//                target = ronova.getTarget();
//            }
//
//            if (target != null) {
//                // 1. Получаем текущую скорость снаряда
//                Vec3d currentVelocity = this.getVelocity();
//
//                // 2. Считаем вектор от снаряда к цели
//                Vec3d targetDir = new Vec3d(
//                        target.getX() - this.getX(),
//                        target.getEyeY() - this.getY(),
//                        target.getZ() - this.getZ()
//                ).normalize().multiply(1.5); // 1.5 — скорость снаряда
//
//                // 3. Плавное наведение (интерполяция): старая скорость + немного нового направления
//                // Чем меньше коэффициент (например, 0.1), тем плавнее и "тяжелее" поворачивает снаряд
//                double smoothness = 0.1;
//                Vec3d newVelocity = new Vec3d(
//                        currentVelocity.x + (targetDir.x - currentVelocity.x) * smoothness,
//                        currentVelocity.y + (targetDir.y - currentVelocity.y) * smoothness,
//                        currentVelocity.z + (targetDir.z - currentVelocity.z) * smoothness
//                );
//
//                // 4. Применяем новую скорость и обновляем направление взгляда снаряда
//                this.setVelocity(newVelocity);
//                this.velocityModified = true;
//            }
//        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(!this.getWorld().isClient) {
            if (entityHitResult.getEntity() instanceof PlayerEntity player && player.isUsingItem() && player.getActiveItem().isOf(Items.SHIELD)) {
                this.discard();
            }
            if (entityHitResult.getEntity() != this.getOwner()) {
                entityHitResult.getEntity().damage(this.getWorld().getDamageSources().magic(),
                        5.0f);

                Random random = this.getWorld().getRandom();
                if(random.nextFloat() <= 0.2f && entityHitResult.getEntity() instanceof PlayerEntity player){
                    player.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 100, 0, false, true));
                }

                this.discard();
            }
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
        if (!this.getWorld().isClient() && this.age > 5) {
            if (!state.isAir()) {
                this.discard(); // или ваша логика разрушения блоков
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
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void pushAwayFrom(Entity entity) {

    }
}
