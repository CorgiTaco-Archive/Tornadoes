package dev.corgitaco.examplemod.client.renderer.entity;

import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.corgitaco.examplemod.client.ModRendertypes;
import dev.corgitaco.examplemod.entity.TornadoEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class TornadoEntityRenderer extends EntityRenderer<TornadoEntity> {
    public static final ResourceLocation LOCATION = new ResourceLocation("textures/block/andesite.png");

    public TornadoEntityRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public boolean shouldRender(TornadoEntity $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        return true;
    }

    @Override
    public void render(TornadoEntity tornado, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        int height = 250;
        renderCloud(matrices, vertexConsumers, height);
        renderTornado(tornado, matrices, vertexConsumers, height);
        super.render(tornado, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private static void renderCloud(PoseStack matrices, MultiBufferSource vertexConsumers, int height) {
        matrices.pushPose();
        VertexConsumer stormCloudBuffer = vertexConsumers.getBuffer(RenderType.beaconBeam(LOCATION, false));
        float r = 0.4F;
        float g = 0.4F;
        float b = 0.4F;
        float a = 1;

        matrices.mulPose(Axis.YP.rotationDegrees((float) GLFW.glfwGetTime() * 1));

        PoseStack.Pose last = matrices.last();
        Matrix4f pose = last.pose();
        int hPoints = 10;


        float cloudSize = 1024F;

        float cloudDeltaIncrement = cloudSize / 24F;
        for (int cloudDelta = 0; cloudDelta < cloudSize; cloudDelta += cloudDeltaIncrement) {
            for (int hPoint = -hPoints; hPoint < hPoints; hPoint++) {
                float nextCloudDelta = cloudDelta + cloudDeltaIncrement;

                float hPct = (float) hPoint / hPoints;
                float nextHPct = (float) (hPoint + 1) / hPoints;

                stormCloudBuffer.vertex(pose, Mth.sin(hPct * Mth.PI) * cloudDelta, height, Mth.cos(hPct * Mth.PI) * cloudDelta).color(r, g, b, a).uv(0, 0).overlayCoords(0, 10).uv2(15728880).normal(last.normal(), 0, 0, 1).endVertex();
                stormCloudBuffer.vertex(pose, Mth.sin(nextHPct * Mth.PI) * cloudDelta, height, Mth.cos(nextHPct * Mth.PI) * cloudDelta).color(r, g, b, a).uv(1, 0).overlayCoords(0, 10).uv2(15728880).normal(last.normal(), 0, 0, 1).endVertex();

                stormCloudBuffer.vertex(pose, Mth.sin(nextHPct * Mth.PI) * nextCloudDelta, height, Mth.cos(nextHPct * Mth.PI) * nextCloudDelta).color(r, g, b, a).uv(1, 1).overlayCoords(0, 10).uv2(15728880).normal(last.normal(), 0, 0, 1).endVertex();
                stormCloudBuffer.vertex(pose, Mth.sin(hPct * Mth.PI) * nextCloudDelta, height, Mth.cos(hPct * Mth.PI) * nextCloudDelta).color(r, g, b, a).uv(0, 1).overlayCoords(0, 10).uv2(15728880).normal(last.normal(), 0, 0, 1).endVertex();
            }
        }
        matrices.popPose();
    }

    private static void renderTornado(TornadoEntity tornado, PoseStack matrices, MultiBufferSource vertexConsumers, int height) {
        matrices.pushPose();
        VertexConsumer tornadoBuffer = vertexConsumers.getBuffer(ModRendertypes.TORNADO.apply(LOCATION, true));
        renderTornado(tornado, matrices, height, 0.3F, 1F, 0.85F, 0.85F, 0.85F, 1, tornadoBuffer, 13, (float) GLFW.glfwGetTime() * 200, true, 100);
        renderTornado(tornado, matrices, height, 0.4F, 0.8F, 0.75F, 0.75F, 0.75F, 1, tornadoBuffer, 11.5F, (float) GLFW.glfwGetTime() * 200, true, 200);
        renderTornado(tornado, matrices, height, 0.5F, 0.6F, 0.6F, 0.6F, 0.6F, 1, tornadoBuffer, 10, (float) GLFW.glfwGetTime() * 200, true, 500);
        matrices.popPose();
    }

    private static void renderTornado(TornadoEntity tornado, PoseStack matrices, int height, float noiseCutOff, float noiseScale, float r, float g, float b, float a, VertexConsumer tornadoBuffer, float startDistance, float rotation, boolean renderInverse, int seed) {

        if (tornadoBuffer instanceof BufferVertexConsumer bufferVertexConsumer) {
            matrices.pushPose();

            LegacyRandomSource randomSource = new LegacyRandomSource(tornado.getUUID().getMostSignificantBits());

            PoseStack.Pose last = matrices.last();
            Matrix4f pose = last.pose();
            matrices.mulPose(Axis.YP.rotationDegrees(rotation));

            float increment = 2;

            float distance = startDistance;

            for (float heightDelta = 0; heightDelta < height; heightDelta += increment) {
                int hPoints = 7;

                float nextDistance = distance + Mth.nextFloat(randomSource, -0.2F, 1F);

                for (int hPoint = -hPoints; hPoint < hPoints; hPoint++) {
                    float hPct = (float) hPoint / hPoints;
                    float nextHPct = (float) (hPoint + 1) / hPoints;
                    float tickCount = (Minecraft.getInstance().level.getGameTime() + seed) * 0.1F;

                    torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 0F, 0F, last, pose, tickCount, Mth.sin(hPct * Mth.PI) * distance, heightDelta, Mth.cos(hPct * Mth.PI) * distance, noiseCutOff, noiseScale);
                    torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 1F, 0F, last, pose, tickCount, Mth.sin(nextHPct * Mth.PI) * distance, heightDelta, Mth.cos(nextHPct * Mth.PI) * distance, noiseCutOff, noiseScale);
                    torandoVertex(tornado, r, g, b, a, bufferVertexConsumer,1F, 1F, last, pose, tickCount, Mth.sin(nextHPct * Mth.PI) * nextDistance, heightDelta + increment, Mth.cos(nextHPct * Mth.PI) * nextDistance, noiseCutOff, noiseScale);
                    torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 0, 1F, last, pose, tickCount, Mth.sin(hPct * Mth.PI) * nextDistance, heightDelta + increment, Mth.cos(hPct * Mth.PI) * nextDistance, noiseCutOff, noiseScale);

                    if (renderInverse) {
                        // Render inverse
                        torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 0, 1F, last, pose, tickCount, Mth.sin(hPct * Mth.PI) * nextDistance, heightDelta + increment, Mth.cos(hPct * Mth.PI) * nextDistance, noiseCutOff, noiseScale);
                        torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 1F, 1F, last, pose, tickCount, Mth.sin(nextHPct * Mth.PI) * nextDistance, heightDelta + increment, Mth.cos(nextHPct * Mth.PI) * nextDistance, noiseCutOff, noiseScale);
                        torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 1F, 0F, last, pose, tickCount, Mth.sin(nextHPct * Mth.PI) * distance, heightDelta, Mth.cos(nextHPct * Mth.PI) * distance, noiseCutOff, noiseScale);
                        torandoVertex(tornado, r, g, b, a, bufferVertexConsumer, 0F, 0F, last, pose, tickCount, Mth.sin(hPct * Mth.PI) * distance, heightDelta, Mth.cos(hPct * Mth.PI) * distance, noiseCutOff, noiseScale);
                    }

                }

                distance = nextDistance;
            }
            matrices.popPose();
        }
    }

    private static void torandoVertex(TornadoEntity tornado, float r, float g, float b, float a, BufferVertexConsumer bufferVertexConsumer, float u, float v, PoseStack.Pose last, Matrix4f pose, float tickCount, float renderX, float renderY, float renderZ, double noiseCutOff, double noiseScale) {
        double worldX = renderX + tornado.getX();
        double worldY = renderY + tornado.getY();
        double worldZ = renderZ + tornado.getZ();
        bufferVertexConsumer.vertex(pose, renderX, renderY, renderZ).color(r, g, b, a).uv(u, v).overlayCoords(0, 10).uv2(15728880).normal(last.normal(), 0, 0, 1).vertex(worldX, worldY, worldZ).vertex(tickCount, noiseCutOff, noiseScale).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(TornadoEntity tornadoEntity) {
        return null;
    }
}
