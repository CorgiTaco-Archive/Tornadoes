package dev.corgitaco.examplemod.client;


import dev.corgitaco.examplemod.client.renderer.entity.TornadoEntityRenderer;
import dev.corgitaco.examplemod.core.ModEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class ModEntityRenderers {

    public static <T extends Entity> void register(RegisterStrategy registerStrategy) {
            registerStrategy.register(ModEntities.TORNADO.get(), TornadoEntityRenderer::new);

    }

    @FunctionalInterface
    public interface RegisterStrategy {
        <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider);
    }
}
