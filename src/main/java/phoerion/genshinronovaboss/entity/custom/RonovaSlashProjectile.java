package phoerion.genshinronovaboss.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.entity.ModEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;

public class RonovaSlashProjectile extends ProjectileEntity implements GeoEntity {
    private int age;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<Entity> hitEntities = new HashSet<>();

    public RonovaSlashProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true; // Полностью отключаем физику столкновений
    }

    public RonovaSlashProjectile(World world, LivingEntity owner) {
        super(ModEntities.RONOVA_SLASH_PROJECTILE, world);
        this.setOwner(owner);
        this.noClip = true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {

        Vec3d velocity = this.getVelocity();
        double nextX = this.getX() + velocity.x;
        double nextY = this.getY() + velocity.y;
        double nextZ = this.getZ() + velocity.z;
        this.setPosition(nextX, nextY, nextZ);

        age++;
        if (age >= 300) {
            this.discard();
            return;
        }

        if (!this.getWorld().isClient()) {
            Box box = this.getBoundingBox();

            // 1. Логика разрушения блоков
            for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
                for (int y = (int) Math.floor(box.minY); y <= (int) Math.floor(box.maxY); y++) {
                    for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = this.getWorld().getBlockState(pos);

                        if (!state.isAir() && !state.isOf(net.minecraft.block.Blocks.BEDROCK)) {
                            this.getWorld().breakBlock(pos, false, this);
                        }
                    }
                }
            }

            // 2. Логика нанесения урона сущностям через собственный хитбокс
            for (LivingEntity target : this.getWorld().getNonSpectatingEntities(LivingEntity.class, box)) {
                if (target != this.getOwner() && !hitEntities.contains(target)) {
                    hitEntities.add(target); // Запоминаем цель, чтобы ударить ровно 1 раз за полет
                    if (target instanceof PlayerEntity player && player.isUsingItem() && player.getActiveItem().isOf(Items.SHIELD)) {
                        target.damage(
                                this.getWorld().getDamageSources().indirectMagic(this, this.getOwner()),
                                15.0f
                        );
                    }
                    else{
                        target.damage(
                                this.getWorld().getDamageSources().indirectMagic(this, this.getOwner()),
                                25.0f
                        );
                    }

                    Random random = this.getWorld().getRandom();
                    if(random.nextFloat() <= 0.5f){
                        target.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 300, 0, false, true));
                    }
                }
            }
        }
    }

    @Override
    public boolean hasNoGravity() {
        return true;
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
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Больше не используем, логика перенесена в tick() для стабильности
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
    }

    @Override
    protected void onBlockCollision(BlockState state) {
    }

    @Override
    public void pushAwayFrom(Entity entity) {

    }

    @Override
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        // Жестко задаем чистый вектор скорости без рандома и ванильных поправок на гравитацию/сопротивление
        super.setVelocity(x, y, z, speed, 0.0f);
        this.velocityModified = true;
    }

    @Override
    public void setOnGround(boolean onGround) {

    }

    @Override
    public void setOnGround(boolean onGround, Vec3d movement) {

    }


}