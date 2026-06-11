package org.hodytrapl.stream_camera.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftPauseMixin {

    //убрать паузу
    @Inject(method = "pauseGame", at = @At("HEAD"), cancellable = true)
    private void disableAutoPause(boolean pauseOnly, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        // Отменяем автоматическую паузу только если окно не в фокусе
        if (mc.isWindowActive()) {
            return; // окно активно — не мешаем нормальной паузе
        }
        ci.cancel();
    }
}