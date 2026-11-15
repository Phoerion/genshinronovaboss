package phoerion.genshinronovaboss.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phoerion.genshinronovaboss.effects.ModEffects;
import phoerion.genshinronovaboss.entity.ai.RonovaAttackGoal;
import phoerion.genshinronovaboss.entity.ai.RonovaNavigation;
import phoerion.genshinronovaboss.client.ClientBossData;
import phoerion.genshinronovaboss.particle.ModParticles;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RonovaEntity extends PathAwareEntity implements GeoEntity {
    private final ServerBossBar bossBar = (ServerBossBar) new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.RED,
            BossBar.Style.PROGRESS
    ).setDarkenSky(true);

    private Set<RonovaEyeEntity> EYES = new HashSet<>();
    public static final Identifier BOSS_STATE_PACKET_ID = new Identifier("genshinronovaboss", "boss_state_packet");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final AnimationState idle = new AnimationState();
    public final AnimationState attack = new AnimationState();
    private boolean isAttackingAnim = false;
    private ChunkPos lastChunkPos;
    public boolean isTryingToEscape = false;
    public boolean triedEscaping = false;
    private int escapeTicks;

    private static final ChunkTicketType<ChunkPos> RONOVA_TICKET = ChunkTicketType.create(
            "ronova_boss",
            Comparator.comparingLong(ChunkPos::toLong)
    );
    private boolean hasShield;

    public RonovaEntity(EntityType<? extends RonovaEntity> entityType, World world) {
        super(entityType, world);

        this.moveControl = new FlightMoveControl(this, 30, true);
        this.setNoGravity(true);
        this.setPersistent();
        this.experiencePoints = 250;
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0D)
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
        if (!this.getWorld().isClient() && this.age % 20 == 0) {
            System.out.println("[RONOVA] alive at " + this.getBlockPos());
        }
        super.tick();
        this.setNoGravity(true);
        if (!this.getWorld().isClient()) {
            if (!this.isAlive()) {
                this.bossBar.clearPlayers();
                return;
            }
        }
        breakBlocksAround();
//        for (PlayerEntity player : this.getWorld().getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(128), e -> e.isAlive())) {
//            disableElytra(player);
//        }
        updateBossBar();
        this.EYES.clear();
        for (RonovaEyeEntity eye : this.getWorld().getEntitiesByClass(RonovaEyeEntity.class, this.getBoundingBox().expand(128), e -> e.isAlive() && e.getRonovaBoss() != null)) {
            if(eye.getRonovaBoss().equals(this)) {
                this.EYES.add(eye);
                if(this.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d eyePos = eye.getEyePos();

                    Vec3d bossPos = this.getPos();
                    Vec3d direction = eyePos.subtract(bossPos);
                    double distance = direction.length();
                    direction = direction.normalize();

                    double step = 1.0;
                    if(this.age % 5 == 0) {
                        for (double d = 0; d < distance; d += step) {
                            double currentX = bossPos.x + direction.x * d;
                            double currentY = bossPos.y + direction.y * d;
                            double currentZ = bossPos.z + direction.z * d;

                            serverWorld.spawnParticles(
                                    ParticleTypes.END_ROD,
                                    currentX, currentY, currentZ,
                                    1, // Количество партиклов в точке
                                    0.0, 0.0, 0.0, 0.0f // Разброс
                            );
                        }
                    }
                }
            }
        }
        if (!this.EYES.isEmpty()) {
            tryCreatingShield();
        }

        this.hasShield = !EYES.isEmpty();

//        //if(!this.triedEscaping) {
//            escapeCheck();
//        }
//
//        if(this.escapeTicks > 0 && this.isTryingToEscape){
//            this.escapeTicks--;
//        }
//
//        if(this.escapeTicks == 80 && this.isTryingToEscape){
//            if (this.getWorld() instanceof ServerWorld serverWorld) {
//                Vec3d look = this.getRotationVector();
//                serverWorld.spawnParticles(
//                        ModParticles.SCARY_RONOVA_EYE_PARTICLE,
//                        this.getX() - look.x * 2, this.getY() + 2.0, this.getZ() - look.z * 2,
//                        1,
//                        0.05, 0.05, 0.05,
//                        0.0
//                );
//            }
//        }

//        if(this.isTryingToEscape && this.escapeTicks == 0){
//            if (!this.getWorld().isClient()) {
//                // Находим всех игроков поблизости (например, в радиусе 32 блоков) или тех, кто участвовал в бою
//                java.util.List<net.minecraft.entity.player.PlayerEntity> players = this.getWorld().getEntitiesByClass(
//                        net.minecraft.entity.player.PlayerEntity.class,
//                        this.getBoundingBox().expand(100.0D),
//                        player -> !player.isSpectator()
//                );
//
//                for (net.minecraft.entity.player.PlayerEntity player : players) {
//                    player.addCommandTag("ronova_cursed");
//                }
//
//
//            }
//
//            this.discard();
//        }

//        if (this.isTryingToEscape) {
//            this.getLookControl().lookAt(this.getX() + this.getRotationVector().x, this.getY() + this.getRotationVector().y, this.getZ() + this.getRotationVector().z);
//        }

        updateFlightHeight();

        updateIsRonovaFightActive();

        defendFromProjectiles();

        updateChunks();
    }

    private void updateBossBar() {
        if (!this.getWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            Box trackingBox = this.getBoundingBox().expand(64.0D);
            List<ServerPlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                    ServerPlayerEntity.class,
                    trackingBox,
                    LivingEntity::isAlive
            );

            for (ServerPlayerEntity player : nearbyPlayers) {
                if (!this.bossBar.getPlayers().contains(player)) {
                    this.bossBar.addPlayer(player);
                }
            }

            List<ServerPlayerEntity> barPlayers = new java.util.ArrayList<>(this.bossBar.getPlayers());
            for (ServerPlayerEntity player : barPlayers) {
                if (!nearbyPlayers.contains(player) || player.isSpectator() || !player.isAlive()) {
                    this.bossBar.removePlayer(player);
                }
            }
        }
    }

    private void disableElytra(PlayerEntity player) {
        if (!player.getWorld().isClient()) {
            ItemStack elytraStack = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);

            if (elytraStack.getItem() instanceof net.minecraft.item.ElytraItem) {
                player.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, ItemStack.EMPTY);

                boolean added = player.getInventory().insertStack(elytraStack);

                if (!added) {
                    player.dropItem(elytraStack, false);
                }
            }
        }
    }

    private void breakBlocksAround() {
        if (this.getWorld().isClient()) return;

        Box box = this.getBoundingBox();
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

    private void tryCreatingShield() {
        if (this.getWorld().isClient() && this.age % 2 == 0) {
            double radius = 1.5;
            int particlesCount = 30;

            for (int i = 0; i < particlesCount; i++) {

                double theta = this.getWorld().random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(this.getWorld().random.nextDouble() * 2 - 1);

                double x = this.getX() + radius * Math.sin(phi) * Math.cos(theta);
                double y = this.getY() + this.getHeight() / 2.0 + radius * Math.sin(phi) * Math.sin(theta);
                double z = this.getZ() + radius * Math.cos(phi);

                this.getWorld().addParticle(ParticleTypes.DRAGON_BREATH, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    private void updateFlightHeight(){
        if (!this.getWorld().isClient()) {
            BlockPos currentPos = this.getBlockPos();
            BlockPos.Mutable mutablePos = currentPos.mutableCopy();

            int groundY = currentPos.getY();
            while (groundY > this.getWorld().getBottomY() && this.getWorld().getBlockState(mutablePos.set(currentPos.getX(), groundY, currentPos.getZ())).isAir()) {
                groundY--;
            }

            double targetY = groundY + 2.0 + 1.0;
            double currentY = this.getY();

            if (Math.abs(currentY - targetY) > 0.1) {
                this.setVelocity(this.getVelocity().x, (targetY - currentY) * 0.1, this.getVelocity().z);
            }
        }
    }

    private void updateIsRonovaFightActive(){
        if (!this.getWorld().isClient()) {
            Box searchBox = this.getBoundingBox().expand(100.0D);
            List<ServerPlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                    ServerPlayerEntity.class,
                    searchBox,
                    LivingEntity::isAlive
            );

            for (ServerPlayerEntity player : nearbyPlayers) {
                ClientBossData.isRonovaFightActive = true;
            }

            if(nearbyPlayers.isEmpty()){
                ClientBossData.isRonovaFightActive = false;
            }
        }
    }

    private void defendFromProjectiles(){
        if (!this.getWorld().isClient()) {
            Box checkbox = this.getBoundingBox().expand(1.5D);

            List<net.minecraft.entity.projectile.PersistentProjectileEntity> projectiles =
                    this.getWorld().getEntitiesByClass(
                            net.minecraft.entity.projectile.PersistentProjectileEntity.class,
                            checkbox,
                            projectile -> !projectile.noClip && projectile.isAlive() &&
                                    (projectile instanceof net.minecraft.entity.projectile.ArrowEntity ||
                                            projectile instanceof net.minecraft.entity.projectile.TridentEntity)
                    );

            for (net.minecraft.entity.projectile.PersistentProjectileEntity projectile : projectiles) {

                Vec3d vel = projectile.getVelocity();
                projectile.setVelocity(-vel.x, -vel.y, -vel.z);

                projectile.velocityModified = true;
            }
        }
    }

    private void updateChunks(){
        if (!this.getWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            ChunkPos currentChunkPos = new ChunkPos(this.getBlockPos());

            if (lastChunkPos != null && !currentChunkPos.equals(lastChunkPos)) {
                serverWorld.setChunkForced(lastChunkPos.x, lastChunkPos.z, false);
            }

            serverWorld.setChunkForced(currentChunkPos.x, currentChunkPos.z, true);

            lastChunkPos = currentChunkPos;
        }
    }

    private void escapeCheck() {
        if(this.getHealth() <= 50){
            this.isTryingToEscape = true;
            this.triedEscaping = true;
            this.escapeTicks = 100;

            performEscapePush();
        }
    }

    private void performEscapePush() {
        if (this.getWorld().isClient()) return;

        Box searchBox = this.getBoundingBox().expand(20.0D);
        List<LivingEntity> entities = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != this && entity.isAlive()
        );

        for (LivingEntity entity : entities) {
            Vec3d dir = entity.getPos().subtract(this.getPos()).normalize();

            entity.setVelocity(dir.x * 2.0, 1.5, dir.z * 2.0);
            entity.damage(this.getWorld().getDamageSources().magic(), 10.0f);

            if(this.getWorld() instanceof ServerWorld serverWorld){
                serverWorld.spawnParticles(
                        ModParticles.PUSH_PARTICLE,
                        this.getX(), this.getY(), this.getZ(),
                        1,
                        0.05, 0.05, 0.05,
                        0.0
                );
            }

            entity.velocityModified = true;

            Random random = this.getWorld().getRandom();
            if(entity instanceof PlayerEntity player){
                player.addStatusEffect(new StatusEffectInstance(ModEffects.RONOVA_CURSE, 600));
            }
        }
    }

    public void triggerAttackAnimation() {
        this.triggerAnim("controller", "attack");// Длительность в тиках (зависит от длины анимации)
    }

    public void triggerTPAttackAnimation() {
        this.triggerAnim("controller", "tpAttack");// Длительность в тиках (зависит от длины анимации)
    }

    public void triggerSlashAttackAnimation() {
        this.triggerAnim("controller", "slashAttack");// Длительность в тиках (зависит от длины анимации)
    }

    public void triggerSummonAttackAnimation() {
        this.triggerAnim("controller", "summon");// Длительность в тиках (зависит от длины анимации)
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if(this.isTryingToEscape) {
                state.getController().setAnimation(RawAnimation.begin().thenPlay("escapeAnimation"));
                return PlayState.CONTINUE;
            }

            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        })
                .triggerableAnim("slashAttack", RawAnimation.begin().thenPlay("slashAttack"))
                .triggerableAnim("tpAttack", RawAnimation.begin().thenPlay("tpAttack"))
                .triggerableAnim("summon", RawAnimation.begin().thenPlay("summon"))
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))); // Регистрируем триггер атаки
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
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.IN_WALL) || source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.ARROW) || source.isOf(DamageTypes.FREEZE)) {
            return false;
        }
        if(source.getAttacker() != null) {
            if (this.isTryingToEscape) {
                this.isTryingToEscape = false;
                this.escapeTicks = 0;
                this.triedEscaping = true;
            }
        }
        if(this.hasShield == true){
            if(source.getAttacker() != null) {
                source.getAttacker().damage(this.getWorld().getDamageSources().magic(), amount / 3);
                amount = amount / 5;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!this.getWorld().isClient()) {
            for (PlayerEntity player : this.getWorld().getPlayers()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(false);
                    ServerPlayNetworking.send(serverPlayer, RonovaEntity.BOSS_STATE_PACKET_ID, buf);
                    this.bossBar.removePlayer((ServerPlayerEntity) player);
                }
            }
        }


    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient()) {
            java.util.List<net.minecraft.entity.player.PlayerEntity> players = this.getWorld().getEntitiesByClass(
                    net.minecraft.entity.player.PlayerEntity.class,
                    this.getBoundingBox().expand(100.0D),
                    player -> !player.isSpectator()
            );

            for (net.minecraft.entity.player.PlayerEntity player : players) {
                player.getCommandTags().remove("ronova_cursed");

                player.removeStatusEffect(phoerion.genshinronovaboss.effects.ModEffects.RONOVA_CURSE);
                this.bossBar.removePlayer((ServerPlayerEntity) player);
            }
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (!this.getWorld().isClient()) {
            this.bossBar.clearPlayers();
        }
    }
}
