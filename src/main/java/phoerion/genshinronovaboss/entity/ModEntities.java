package phoerion.genshinronovaboss.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;

public class ModEntities {
    public static final EntityType<RonovaEntity> RONOVA_BOSS = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(GenshinRonovaBoss.MOD_ID, "ronova"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, RonovaEntity::new).dimensions(EntityDimensions.fixed(1.0f, 2.0f)).build());

    public static final EntityType<RonovaProjectileEntity> PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("genshinronovaboss", "projectile"),
            FabricEntityTypeBuilder.<RonovaProjectileEntity>create(SpawnGroup.MISC, RonovaProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.6f))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(10)
                    .build()
    );

    public static void registerEntities(){
        GenshinRonovaBoss.LOGGER.info("Registering Entities for " + GenshinRonovaBoss.MOD_ID);
    }

}
