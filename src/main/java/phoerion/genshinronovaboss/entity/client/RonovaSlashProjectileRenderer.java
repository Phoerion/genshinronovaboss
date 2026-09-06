package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaSlashProjectileModel;
import phoerion.genshinronovaboss.entity.custom.RonovaSlashProjectile;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RonovaSlashProjectileRenderer extends GeoEntityRenderer<RonovaSlashProjectile> {
    public RonovaSlashProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RonovaSlashProjectileModel());
        this.shadowRadius = 1.0f;
        this.withScale(5.0f);
    }
}
