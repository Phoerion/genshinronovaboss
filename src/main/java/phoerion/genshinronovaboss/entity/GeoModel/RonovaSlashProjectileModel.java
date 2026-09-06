package phoerion.genshinronovaboss.entity.GeoModel;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import phoerion.genshinronovaboss.entity.custom.RonovaSlashProjectile;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RonovaSlashProjectileModel extends GeoModel<RonovaSlashProjectile> {

    @Override
    public Identifier getModelResource(RonovaSlashProjectile animatable) {
        return new Identifier(GenshinRonovaBoss.MOD_ID, "geo/ronova_slash_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(RonovaSlashProjectile animatable) {
        return new Identifier(GenshinRonovaBoss.MOD_ID, "textures/entity/ronova_slash_projectile.png");
    }

    @Override
    public Identifier getAnimationResource(RonovaSlashProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(RonovaSlashProjectile animatable, long instanceId, AnimationState<RonovaSlashProjectile> animationState) {
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

        bone.setRotY(-yaw - 1.57079f - 1.57079f - 1.57079f);
        bone.setRotZ(pitch);
    }
}
