package phoerion.genshinronovaboss.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.entity.ai.RonovaAttackGoal;
import phoerion.genshinronovaboss.entity.ai.RonovaNavigation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class RonovaEntity extends PathAwareEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final AnimationState idle = new AnimationState();
    public final AnimationState attack = new AnimationState();
    private boolean isAttackingAnim = false;

    public RonovaEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.moveControl = new FlightMoveControl(this, 30, true);
        this.setNoGravity(true);
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new RonovaNavigation(this, world);
        birdNavigation.setCanEnterOpenDoors(true);
        birdNavigation.setCanPathThroughDoors(true);
        birdNavigation.setSpeed(1f);
        return birdNavigation;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected int getNextAirUnderwater(int air) {
        return 10;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {

    }


    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 1.0D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1000.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1000.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new RonovaAttackGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        //if(this.isOnGround())

        if (!this.getWorld().isClient()) {
            BlockPos currentPos = this.getBlockPos();
            BlockPos.Mutable mutablePos = currentPos.mutableCopy();

            // Ищем ближайший твердый блок под мобом
            int groundY = currentPos.getY();
            while (groundY > this.getWorld().getBottomY() && this.getWorld().getBlockState(mutablePos.set(currentPos.getX(), groundY, currentPos.getZ())).isAir()) {
                groundY--;
            }

            double targetY = groundY + 2.0 + 1.0; // Высота земли + 2 блока воздуха + смещение до центра моба
            double currentY = this.getY();

            // Если босс отклонился по высоте, плавно корректируем его положение
            if (Math.abs(currentY - targetY) > 0.1) {
                this.setVelocity(this.getVelocity().x, (targetY - currentY) * 0.1, this.getVelocity().z);
            }
        }

    }

    public void triggerAttackAnimation() {
        this.triggerAnim("controller", "attack");// Длительность в тиках (зависит от длины анимации)
    }

    public void triggerTPAttackAnimation() {
        this.triggerAnim("controller", "tpAttack");// Длительность в тиках (зависит от длины анимации)
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // Базовая анимация по умолчанию
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }).triggerableAnim("tpAttack", RawAnimation.begin().thenPlay("tpAttack")).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))); // Регистрируем триггер атаки
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isInsideWall() {
        this.getPos().add(0.0f, 1.0f, 0.0f);
        return false;
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        // Если источник урона — удушье внутри стены, полностью отменяем урон
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.IN_WALL) || source.isOf(DamageTypes.EXPLOSION)) {
            return false;
        }
        return super.damage(source, amount);
    }
}
