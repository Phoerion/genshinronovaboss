package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaEyeEntityModel;
import phoerion.genshinronovaboss.entity.custom.RonovaEyeEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RonovaEyeEntityRenderer extends GeoEntityRenderer<RonovaEyeEntity> {
    public RonovaEyeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RonovaEyeEntityModel());
        this.shadowRadius = 1.0f;
        this.withScale(2.0f);
    }
}
