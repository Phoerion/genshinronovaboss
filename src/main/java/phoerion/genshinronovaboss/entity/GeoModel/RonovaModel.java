package phoerion.genshinronovaboss.entity.GeoModel;

import net.minecraft.util.Identifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import software.bernie.geckolib.model.GeoModel;

public class RonovaModel extends GeoModel<RonovaEntity> {
    @Override
    public Identifier getModelResource(RonovaEntity animatable) {
        return new Identifier(GenshinRonovaBoss.MOD_ID, "geo/ronova.geo.json");
    }

    @Override
    public Identifier getTextureResource(RonovaEntity animatable) {
        return new Identifier(GenshinRonovaBoss.MOD_ID, "textures/entity/ronova.png");
    }

    @Override
    public Identifier getAnimationResource(RonovaEntity animatable) {
        return new Identifier(GenshinRonovaBoss.MOD_ID, "animations/ronova.animation.json");
    }
}
