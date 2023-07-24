package dev.corgitaco.examplemod.mixin.client;

import com.mojang.datafixers.util.Pair;
import dev.corgitaco.examplemod.client.ModRendertypes;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void appendShaders(ResourceProvider $$0, CallbackInfo ci, List $$1, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaders) throws IOException {
        shaders.add(Pair.of(new ShaderInstance($$0, "rendertype_tornado", ModRendertypes.TORNADO_FORMAT), (shaderInstance) -> {
            ModRendertypes.rendertypeTornadoShader = shaderInstance;
        }));
    }
}
