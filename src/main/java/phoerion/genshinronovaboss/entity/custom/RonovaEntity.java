package phoerion.genshinronovaboss.entity.custom;

import net.minecraft.block.BlockState;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.entity.ai.RonovaAttackGoal;
import phoerion.genshinronovaboss.entity.ai.RonovaNavigation;



public class RonovaEntity extends PathAwareEntity {
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1000.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50.0D);
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
}
