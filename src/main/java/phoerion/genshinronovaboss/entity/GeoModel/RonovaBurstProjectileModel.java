package phoerion.genshinronovaboss.entity.GeoModel;

import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.entity.custom.RonovaBurstProjectile;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RonovaBurstProjectileModel extends GeoModel<RonovaBurstProjectile> {

    @Override
    public Identifier getModelResource(RonovaBurstProjectile animatable) {
        return new Identifier("genshinronovaboss", "geo/ronova_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(RonovaBurstProjectile animatable) {
        return new Identifier("genshinronovaboss", "textures/entity/ronova_busrt_projectile.png");
    }

    @Override
    public Identifier getAnimationResource(RonovaBurstProjectile animatable) {
        return null;
    }

}
