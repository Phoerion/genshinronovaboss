package phoerion.genshinronovaboss.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.entity.ModEntities;
import phoerion.genshinronovaboss.entity.ai.RonovaAttackGoal;
import phoerion.genshinronovaboss.entity.ai.RonovaNavigation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class RonovaEyeEntity extends PathAwareEntity implements GeoEntity {
    private static final TrackedData<Optional<UUID>> RONOVA_UUID = DataTracker.registerData(RonovaEyeEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RonovaEyeEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(ModEntities.RONOVA_EYE_ENTITY, world);

    }

    public RonovaEyeEntity(World world, LivingEntity owner) {
        super(ModEntities.RONOVA_EYE_ENTITY, world);
        if (owner != null) {
            this.setRonova(owner);
        }
    }


    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new RonovaNavigation(this, world);
        birdNavigation.setCanEnterOpenDoors(true);
        birdNavigation.setCanPathThroughDoors(true);
        birdNavigation.setSpeed(1f);
        return birdNavigation;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 1.0D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D);
    }

    public void setRonova(LivingEntity ronova){
        if (this.dataTracker != null) {
            this.dataTracker.set(RONOVA_UUID, Optional.of(ronova.getUuid()));
        }
    }

    @Override
    public void tick() {
        if(this.age >= 900){
            this.discard();
        }
        super.tick();
        RonovaEntity boss = getRonovaBoss();
        if (boss != null) {
            if (boss.getTarget() != null && this.getTarget() != boss.getTarget()) {
                this.setTarget(boss.getTarget());
            }
        }
        this.setNoGravity(true);
        updateFlightHeight();
        if (!this.getWorld().isClient() && this.age % 20 == 0) {
            LivingEntity ronova = getRonovaBoss();

            if (ronova != null && ronova.isAlive()) {
                ronova.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 3, true, false));
            } else if (this.age > 100) {
                this.discard();
            }
        }

        if (this.getWorld().isClient()) return;

        Box box = this.getBoundingBox().expand(0.5f);
        ServerWorld serverWorld = (ServerWorld) this.getWorld();

        for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y <= (int) Math.floor(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = serverWorld.getBlockState(pos);

                    if (!state.isAir() && !state.isOf(net.minecraft.block.Blocks.BEDROCK)) {
                        serverWorld.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);

                        serverWorld.getChunkManager().markForUpdate(pos);
                    }
                }
            }
        }

    }

    public RonovaEntity getRonovaBoss() {
        if (this.dataTracker == null) return null;

        Optional<UUID> uuid = this.dataTracker.get(RONOVA_UUID);
        if (uuid == null || uuid.isEmpty()) return null;

        World world = this.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(uuid.get());
            if (entity instanceof RonovaEntity living) return living;
        } else {
            for (Entity entity : world.getEntitiesByClass(RonovaEntity.class, this.getBoundingBox().expand(128.0), e -> true)) {
                if (entity.getUuid().equals(uuid.get()) && entity instanceof RonovaEntity living) {
                    return living;
                }
            }
        }
        return null;
    }

    private void updateFlightHeight(){
        if (!this.getWorld().isClient() && this.getRonovaBoss() != null) {

            int ronovaY = (int) this.getRonovaBoss().getY();

            double targetY = ronovaY;
            double currentY = this.getY();

            if (Math.abs(currentY - targetY) > 0.1) {
                this.setVelocity(this.getVelocity().x, (targetY - currentY) * 0.1, this.getVelocity().z);
            }
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(2, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RONOVA_UUID, Optional.empty());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.isOf(DamageTypes.EXPLOSION)){
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public void pushAwayFrom(Entity entity) {

    }
}
