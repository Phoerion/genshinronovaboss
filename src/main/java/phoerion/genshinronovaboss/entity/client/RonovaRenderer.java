package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import phoerion.genshinronovaboss.GenshinRonovaBoss;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;

public class RonovaRenderer extends MobEntityRenderer<RonovaEntity, RonovaModel> {
    private static final Identifier TEXTURE = new Identifier(GenshinRonovaBoss.MOD_ID, "textures/entity/ronova.png");

    public RonovaRenderer(EntityRendererFactory.Context context) {
        super(context, new RonovaModel(context.getPart(ModModelLayers.RONOVA)), 0.6f);
    }

    @Override
    public Identifier getTexture(RonovaEntity entity) {
        return TEXTURE;
    }
}
