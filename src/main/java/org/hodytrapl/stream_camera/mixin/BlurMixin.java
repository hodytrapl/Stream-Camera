package org.hodytrapl.stream_camera.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public class BlurMixin {
    //убрать долбанный блюр
    @Inject(method = "loadEffect(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void disableBlur(ResourceLocation shader, CallbackInfo ci) {

        if (shader != null && shader.getPath().contains("blur")) {
            ci.cancel();
        }
    }

}
