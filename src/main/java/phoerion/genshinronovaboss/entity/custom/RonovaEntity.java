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
        super.tick();
        this.setNoGravity(true);
        breakBlocksAround();
        for (PlayerEntity player : this.getWorld().getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(128), e -> e.isAlive())) {
            disableElytra(player);
        }
        updateBossBar();
        this.EYES.clear(); // Важно очищать список каждый тик, чтобы старые/мертвые глаза удалялись
        for (RonovaEyeEntity eye : this.getWorld().getEntitiesByClass(RonovaEyeEntity.class, this.getBoundingBox().expand(128), e -> e.isAlive() && e.getRonovaBoss() != null)) {
            if(eye.getRonovaBoss().equals(this)) {
                this.EYES.add(eye);
                if(this.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d eyePos = eye.getEyePos(); // Центр глаза миньона

                    // Вычисляем вектор от босса до глаза
                    Vec3d bossPos = this.getPos();
                    Vec3d direction = eyePos.subtract(bossPos);
                    double distance = direction.length();
                    direction = direction.normalize();

                    // Шаг расстановки партиклов (чем меньше, тем сплошнее будет луч)
                    double step = 1.0;
                    if(this.age % 5 == 0) {
                        for (double d = 0; d < distance; d += step) {
                            double currentX = bossPos.x + direction.x * d;
                            double currentY = bossPos.y + direction.y * d;
                            double currentZ = bossPos.z + direction.z * d;

                            // Спавним партикл на каждой точке луча
                            serverWorld.spawnParticles(
                                    ParticleTypes.END_ROD, // Можно заменить на ваш кастомный партикл
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

        if(!this.triedEscaping) {
            escapeCheck();
        }

        if(this.escapeTicks > 0 && this.isTryingToEscape){
            this.escapeTicks--;
        }

        if(this.escapeTicks == 80 && this.isTryingToEscape){
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d look = this.getRotationVector();
                serverWorld.spawnParticles(
                        ModParticles.SCARY_RONOVA_EYE_PARTICLE,
                        this.getX() - look.x * 2, this.getY() + 2.0, this.getZ() - look.z * 2,
                        1,
                        0.05, 0.05, 0.05,
                        0.0
                );
            }
        }

        if(this.isTryingToEscape && this.escapeTicks == 0){
            if (!this.getWorld().isClient()) {
                // Находим всех игроков поблизости (например, в радиусе 32 блоков) или тех, кто участвовал в бою
                java.util.List<net.minecraft.entity.player.PlayerEntity> players = this.getWorld().getEntitiesByClass(
                        net.minecraft.entity.player.PlayerEntity.class,
                        this.getBoundingBox().expand(100.0D),
                        player -> !player.isSpectator()
                );

                for (net.minecraft.entity.player.PlayerEntity player : players) {
                    player.addCommandTag("ronova_cursed");
                }


            }

            this.discard();
        }

        if (this.isTryingToEscape) {
            this.getLookControl().lookAt(this.getX() + this.getRotationVector().x, this.getY() + this.getRotationVector().y, this.getZ() + this.getRotationVector().z);
        }

        updateFlightHeight();

        updateIsRonovaFightActive();

        defendFromProjectiles();

        updateChunks();
    }

    private void updateBossBar() {
        if (!this.getWorld().isClient()) {
            // Обновляем значение прогресса полоски на основе текущего здоровья босса
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            // Управляем игроками в радиусе видимости (например, 64 блоков)
            Box trackingBox = this.getBoundingBox().expand(64.0D);
            List<ServerPlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                    ServerPlayerEntity.class,
                    trackingBox,
                    LivingEntity::isAlive
            );

            // Добавляем новых игроков в область видимости босс-бара
            for (ServerPlayerEntity player : nearbyPlayers) {
                if (!this.bossBar.getPlayers().contains(player)) {
                    this.bossBar.addPlayer(player);
                }
            }

            // Удаляем из бара игроков, которые отошли далеко или умерли
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
                // Очищаем слот
                player.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, ItemStack.EMPTY);

                // Пытаемся положить в инвентарь игрока
                boolean added = player.getInventory().insertStack(elytraStack);

                // Если инвентарь полон, дропаем предмет на землю возле игрока
                if (!added) {
                    player.dropItem(elytraStack, false);
                }
            }
        }
    }

    private void breakBlocksAround() {
        if (this.getWorld().isClient()) return;

        Box box = this.getBoundingBox().expand(0.5f);
        ServerWorld serverWorld = (ServerWorld) this.getWorld();

        for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y <= (int) Math.floor(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = serverWorld.getBlockState(pos);

                    if (!state.isAir() && !state.isOf(net.minecraft.block.Blocks.BEDROCK)) {
                        // 1. Полностью сносим блок на сервере
                        serverWorld.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);

                        // 2. Принудительно отправляем пакет всем игрокам поблизости,
                        // чтобы клиент гарантированно стер коллизию и обновил рендер
                        serverWorld.getChunkManager().markForUpdate(pos);
                    }
                }
            }
        }
    }

    private void tryCreatingShield() {
        if (this.getWorld().isClient() && this.age % 2 == 0) { // Каждые 2 тика для оптимизации
            double radius = 1.5; // Радиус щита
            int particlesCount = 30; // Количество точек на сфере за один спавн

            for (int i = 0; i < particlesCount; i++) {
                // Углы для равномерного распределения по сфере (Fibonacci sphere или случайные)
                double theta = this.getWorld().random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(this.getWorld().random.nextDouble() * 2 - 1);

                double x = this.getX() + radius * Math.sin(phi) * Math.cos(theta);
                double y = this.getY() + this.getHeight() / 2.0 + radius * Math.sin(phi) * Math.sin(theta);
                double z = this.getZ() + radius * Math.cos(phi);

                // Спавним партикл (например, портала, магии или кастомный)
                this.getWorld().addParticle(ParticleTypes.DRAGON_BREATH, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    private void updateFlightHeight(){
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

    private void updateIsRonovaFightActive(){
        if (!this.getWorld().isClient()) {
            // Задаем область поиска 64 блока вокруг босса
            Box searchBox = this.getBoundingBox().expand(100.0D);
            List<ServerPlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                    ServerPlayerEntity.class,
                    searchBox,
                    LivingEntity::isAlive
            );

            // Проходим по всем игрокам в радиусе
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
            // Проверяем снаряды в радиусе защиты вокруг босса
            Box checkbox = this.getBoundingBox().expand(1.5D);

            // Сканируем только стрелы (ArrowEntity / PersistentProjectileEntity) и трезубцы (TridentEntity)
            List<net.minecraft.entity.projectile.PersistentProjectileEntity> projectiles =
                    this.getWorld().getEntitiesByClass(
                            net.minecraft.entity.projectile.PersistentProjectileEntity.class,
                            checkbox,
                            projectile -> !projectile.noClip && projectile.isAlive() &&
                                    (projectile instanceof net.minecraft.entity.projectile.ArrowEntity ||
                                            projectile instanceof net.minecraft.entity.projectile.TridentEntity)
                    );

            for (net.minecraft.entity.projectile.PersistentProjectileEntity projectile : projectiles) {
                // Инвертируем вектор движения, заставляя снаряд отскочить назад
                Vec3d vel = projectile.getVelocity();
                projectile.setVelocity(-vel.x, -vel.y, -vel.z);

                projectile.velocityModified = true;

                // Опционально: можно сделать так, чтобы отлетевший трезубец или стрела наносили урон тому, кто их кинул
                // projectile.setOwner(this);
            }
        }
    }

    private void updateChunks(){
        if (!this.getWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            ChunkPos currentChunkPos = new ChunkPos(this.getBlockPos());

            // Если босс сменил чанк — убираем старый форс-лоуд
            if (lastChunkPos != null && !currentChunkPos.equals(lastChunkPos)) {
                serverWorld.setChunkForced(lastChunkPos.x, lastChunkPos.z, false);
            }

            // Каждые 20 тиков (раз в секунду) или принудительно обновляем статус
            // текущего чанка, чтобы он никогда не выгрузился под боссом
            serverWorld.setChunkForced(currentChunkPos.x, currentChunkPos.z, true);

            lastChunkPos = currentChunkPos;

            // ... здесь дальше идет ваша логика поиска игроков и пакетов ...
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

        // Радиус отталкивания (например, 8 блоков вокруг босса)
        Box searchBox = this.getBoundingBox().expand(20.0D);
        List<LivingEntity> entities = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != this && entity.isAlive()
        );

        for (LivingEntity entity : entities) {
            // Вычисляем вектор направления от босса к сущности
            Vec3d dir = entity.getPos().subtract(this.getPos()).normalize();

            // Задаем силу отталкивания (по горизонтали умножаем на 1.5, добавляем подброс вверх на 0.4)
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

            // Обязательно помечаем, что скорость изменена, чтобы сервер синхронизировал ее с клиентом
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
            // Базовая анимация по умолчанию
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
        // Если источник урона — удушье внутри стены, полностью отменяем урон
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.IN_WALL) || source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.ARROW) || source.isOf(DamageTypes.FREEZE)) {
            return false;
        }
        if(source.getAttacker() != null) {
            if (this.isTryingToEscape) {
                this.isTryingToEscape = false;
                this.escapeTicks = 0;
                this.triedEscaping = true; // Запрещаем повторный запуск побега до конца боя (по желанию)
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
                    buf.writeBoolean(false); // Выключаем бой
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
            // Находим всех игроков поблизости (например, в радиусе 32 блоков) или тех, кто участвовал в бою
            java.util.List<net.minecraft.entity.player.PlayerEntity> players = this.getWorld().getEntitiesByClass(
                    net.minecraft.entity.player.PlayerEntity.class,
                    this.getBoundingBox().expand(100.0D),
                    player -> !player.isSpectator()
            );

            for (net.minecraft.entity.player.PlayerEntity player : players) {
                // Удаляем постоянный тег проклятия
                player.getCommandTags().remove("ronova_cursed");

                // Снимаем сам эффект статуса
                player.removeStatusEffect(phoerion.genshinronovaboss.effects.ModEffects.RONOVA_CURSE);
            }
        }
    }
}
