package com.cuboidlabs.soulscythe.rendering;

import com.cuboidlabs.soulscythe.SoulScythe;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ChainRendering extends FeatureRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> {

    public ChainRendering(FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

    }
}
