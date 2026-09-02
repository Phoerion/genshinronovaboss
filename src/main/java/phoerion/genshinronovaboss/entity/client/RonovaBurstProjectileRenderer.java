package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaBurstProjectileModel;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaProjectileModel;
import phoerion.genshinronovaboss.entity.custom.RonovaBurstProjectile;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RonovaBurstProjectileRenderer extends GeoEntityRenderer<RonovaBurstProjectile> {

    public RonovaBurstProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RonovaBurstProjectileModel());
        this.shadowRadius = 0;
        this.withScale(5.0f);


        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));

    }

}

