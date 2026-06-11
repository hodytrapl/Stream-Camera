package org.hodytrapl.stream_camera.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    //мменю нахуй
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void blockPauseScreen(Screen screen, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        // Если окно в фокусе — не блокируем, даём открыть меню паузы
        if (mc.isWindowActive() || !(screen instanceof PauseScreen)) {
            return;
        }
        // Если окно не в фокусе и экран — PauseScreen — отменяем
        ci.cancel();
    }
}