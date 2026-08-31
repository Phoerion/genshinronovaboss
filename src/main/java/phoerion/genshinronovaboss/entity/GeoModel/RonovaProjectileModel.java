package phoerion.genshinronovaboss.entity.GeoModel;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RonovaProjectileModel extends GeoModel<RonovaProjectileEntity> {

    @Override
    public Identifier getModelResource(RonovaProjectileEntity animatable) {
        return new Identifier("genshinronovaboss", "geo/ronova_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(RonovaProjectileEntity animatable) {
        return new Identifier("genshinronovaboss", "textures/entity/ronova_projectile.png");
    }

    @Override
    public Identifier getAnimationResource(RonovaProjectileEntity animatable) {
        return new Identifier("genshinronovaboss", "animations/fly.animation.json");
    }

    @Override
    public void setCustomAnimations(
            RonovaProjectileEntity animatable,
            long instanceId,
            AnimationState<RonovaProjectileEntity> animationState
    ) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone bone = getBone("bb_main").orElse(null);

        if (bone == null)
            return;

        Vec3d velocity = animatable.getVelocity();

        if (velocity.lengthSquared() < 0.000001)
            return;

        velocity = velocity.normalize();

        float yaw = (float)Math.atan2(velocity.z, velocity.x);
        float pitch = (float)Math.atan2(
                velocity.y,
                Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)
        );

        bone.setRotY(-yaw);
        bone.setRotZ(pitch);
    }
}



