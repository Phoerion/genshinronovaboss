package phoerion.genshinronovaboss.entity.GeoModel;

import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.entity.custom.RonovaEyeEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class RonovaEyeEntityModel extends GeoModel<RonovaEyeEntity> {
    @Override
    public Identifier getModelResource(RonovaEyeEntity animatable) {
        return new Identifier("genshinronovaboss", "geo/ronova_eye_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(RonovaEyeEntity animatable) {
        return new Identifier("genshinronovaboss", "textures/entity/ronova_eye.png");
    }

    @Override
    public Identifier getAnimationResource(RonovaEyeEntity animatable) {
        return new Identifier("genshinronovaboss", "animations/ronova_eye_entity.animation.json");
    }
}
