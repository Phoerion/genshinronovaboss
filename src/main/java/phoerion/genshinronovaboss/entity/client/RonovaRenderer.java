package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaModel;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaProjectileModel;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RonovaRenderer extends GeoEntityRenderer<RonovaEntity> {

    public RonovaRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RonovaModel());
        this.shadowRadius = 0.8f;
        this.withScale(1.0f);

    }
}
