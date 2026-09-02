package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.GeoModel.RonovaProjectileModel;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;
import phoerion.genshinronovaboss.entity.custom.RonovaProjectileEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RonovaProjectileRenderer extends GeoEntityRenderer<RonovaProjectileEntity> {

    public RonovaProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RonovaProjectileModel());
        this.shadowRadius = 0;
        this.withScale(5.0f);

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));

    }

}

