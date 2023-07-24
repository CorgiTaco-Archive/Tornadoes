package dev.corgitaco.examplemod.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
public class ModRendertypes {


    @Nullable
    public static ShaderInstance rendertypeTornadoShader;

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TORNADO = new RenderStateShard.ShaderStateShard(() -> rendertypeTornadoShader);

    public static final VertexFormat TORNADO_FORMAT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
            .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
            .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
            .put("UV0", DefaultVertexFormat.ELEMENT_UV0)
            .put("UV2", DefaultVertexFormat.ELEMENT_UV2)
            .put("Normal", DefaultVertexFormat.ELEMENT_NORMAL)
            .put("Padding", DefaultVertexFormat.ELEMENT_PADDING)
            .put("WorldPos", DefaultVertexFormat.ELEMENT_POSITION)
            .put("Settings", DefaultVertexFormat.ELEMENT_POSITION)
            .build()
    );



    public static final BiFunction<ResourceLocation, Boolean, RenderType> TORNADO = (p_234330_, p_234331_) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TORNADO).setTextureState(new RenderStateShard.TextureStateShard(p_234330_, false, false)).setTransparencyState(RenderType.NO_TRANSPARENCY).setWriteMaskState(RenderType.COLOR_DEPTH_WRITE).createCompositeState(true);
        return RenderType.create("tornado", TORNADO_FORMAT, VertexFormat.Mode.QUADS, 256, false, false, rendertype$compositestate);
    };
}
