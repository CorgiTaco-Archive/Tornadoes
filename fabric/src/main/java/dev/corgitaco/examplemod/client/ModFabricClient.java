package dev.corgitaco.examplemod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityRenderers.register(EntityRendererRegistry::register);
    }
}
