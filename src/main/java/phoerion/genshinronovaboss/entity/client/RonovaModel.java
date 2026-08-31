package phoerion.genshinronovaboss.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import phoerion.genshinronovaboss.entity.custom.RonovaEntity;

public class RonovaModel extends EntityModel<RonovaEntity> {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart hair_back;
	private final ModelPart hair_left;
	private final ModelPart hair_right;
	private final ModelPart hair_top;
	private final ModelPart hair_front;
	private final ModelPart hair_right2;
	private final ModelPart hair_left2;
	private final ModelPart right_arm;
	private final ModelPart skirt_back;
	private final ModelPart skirt_left;
	private final ModelPart skirt_right;
	private final ModelPart wing_left;
	private final ModelPart wing_right;
	private final ModelPart left_arm;
	private final ModelPart chest;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public RonovaModel(ModelPart root) {
		// Получаем главную кость root
		this.root = root.getChild("root");

		// Дочерние элементы root
		this.body = this.root.getChild("body");
		this.left_leg = this.root.getChild("left_leg");
		this.right_leg = this.root.getChild("right_leg");

		// Дочерние элементы body
		this.head = this.body.getChild("head");
		this.right_arm = this.body.getChild("right_arm");
		this.left_arm = this.body.getChild("left_arm");
		this.skirt_back = this.body.getChild("skirt_back");
		this.skirt_left = this.body.getChild("skirt_left");
		this.skirt_right = this.body.getChild("skirt_right");
		this.wing_left = this.body.getChild("wing_left");
		this.wing_right = this.body.getChild("wing_right");
		this.chest = this.body.getChild("chest");

		// Дочерние элементы head
		this.hair_back = this.head.getChild("hair_back");
		this.hair_left = this.head.getChild("hair_left");
		this.hair_right = this.head.getChild("hair_right");
		this.hair_top = this.head.getChild("hair_top");
		this.hair_front = this.head.getChild("hair_front");

		// Дочерние элементы hair_front
		this.hair_right2 = this.hair_front.getChild("hair_right2");
		this.hair_left2 = this.hair_front.getChild("hair_left2");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(72, 40).cuboid(-3.5F, 0.0F, -2.0F, 7.0F, 12.0F, 4.0F, new Dilation(0.0F))
				.uv(72, 75).cuboid(-3.0F, 10.0F, -3.0F, 6.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -24.0F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(36, 61).cuboid(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F))
				.uv(38, 30).cuboid(-3.0F, -3.0F, -3.6F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(38, 30).cuboid(-1.0F, -2.0F, 4.6F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -1.0F, 1.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData hair_back = head.addChild("hair_back", ModelPartBuilder.create().uv(0, 0).cuboid(-8.5F, -4.0F, -0.5F, 16.0F, 30.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 3.5F, 0.1745F, 0.0F, 0.0F));

		ModelPartData hair_left = head.addChild("hair_left", ModelPartBuilder.create(), ModelTransform.of(3.5F, 0.0F, -1.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData hair_left_r1 = hair_left.addChild("hair_left_r1", ModelPartBuilder.create().uv(38, 0).cuboid(-1.1609F, -1.9316F, -1.8695F, 2.0F, 23.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -6.0F, 1.0F, 0.1234F, -0.0853F, -0.1006F));

		ModelPartData hair_right = head.addChild("hair_right", ModelPartBuilder.create(), ModelTransform.of(-3.5F, 0.0F, -1.0F, 0.0F, 0.0F, 0.2618F));

		ModelPartData hair_right_r1 = hair_right.addChild("hair_right_r1", ModelPartBuilder.create().uv(56, 0).cuboid(0.1609F, -1.9316F, -1.8695F, 2.0F, 23.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -6.0F, 1.0F, 0.1234F, 0.0853F, 0.1006F));

		ModelPartData hair_top = head.addChild("hair_top", ModelPartBuilder.create().uv(72, 30).cuboid(-3.0F, -8.0F, -4.0F, 6.0F, 1.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData hair_front = head.addChild("hair_front", ModelPartBuilder.create().uv(72, 79).cuboid(-3.0F, -7.0F, -4.5F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F))
				.uv(72, 84).cuboid(-3.0F, -8.0F, -4.0F, 6.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData hair_right2 = hair_front.addChild("hair_right2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r2 = hair_right2.addChild("cube_r2", ModelPartBuilder.create().uv(56, 75).cuboid(-1.4677F, -0.5988F, 0.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -7.0F, -4.0F, 0.0F, 0.0F, -0.2182F));

		ModelPartData hair_left2 = hair_front.addChild("hair_left2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r3 = hair_left2.addChild("cube_r3", ModelPartBuilder.create().uv(64, 75).cuboid(-1.673F, -0.5164F, 0.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -7.0F, -4.0F, 0.0F, 0.0F, 0.2618F));

		ModelPartData right_arm = body.addChild("right_arm", ModelPartBuilder.create().uv(46, 75).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.5F, 2.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		ModelPartData skirt_back = body.addChild("skirt_back", ModelPartBuilder.create().uv(64, 61).cuboid(-5.0F, -4.0F, -1.0F, 10.0F, 11.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 61).cuboid(-8.0F, 7.0F, -1.0F, 15.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 12.0F, 2.0F, 0.2618F, 0.0F, 0.0F));

		ModelPartData skirt_left = body.addChild("skirt_left", ModelPartBuilder.create().uv(0, 70).cuboid(-1.5F, -2.0F, -2.5F, 3.0F, 16.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(3.5F, 12.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

		ModelPartData skirt_right = body.addChild("skirt_right", ModelPartBuilder.create().uv(18, 70).cuboid(-1.5F, -2.0F, -2.5F, 3.0F, 16.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, 12.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

		ModelPartData wing_left = body.addChild("wing_left", ModelPartBuilder.create().uv(0, 33).cuboid(-0.5774F, -20.0F, 0.9063F, 18.0F, 28.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 4.0F, 3.0F, 0.0F, -0.4363F, -0.2618F));

		ModelPartData wing_right = body.addChild("wing_right", ModelPartBuilder.create().uv(36, 33).cuboid(-17.4226F, -20.0F, 0.9063F, 18.0F, 28.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 4.0F, 3.0F, 0.0F, 0.4363F, 0.2618F));

		ModelPartData left_arm = body.addChild("left_arm", ModelPartBuilder.create().uv(36, 75).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, 2.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		ModelPartData chest = body.addChild("chest", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r4 = chest.addChild("cube_r4", ModelPartBuilder.create().uv(72, 56).cuboid(-3.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, -2.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData left_leg = root.addChild("left_leg", ModelPartBuilder.create().uv(74, 0).cuboid(-1.0F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(1.5F, -12.0F, 0.0F));

		ModelPartData right_leg = root.addChild("right_leg", ModelPartBuilder.create().uv(74, 15).cuboid(-2.0F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.5F, -12.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(RonovaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}