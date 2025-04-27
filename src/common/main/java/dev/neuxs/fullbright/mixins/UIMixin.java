package dev.neuxs.fullbright.mixins;

import dev.neuxs.fullbright.Mod;
import finalforeach.cosmicreach.ui.UI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(UI.class)
public class UIMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(CallbackInfo ci) {
        Mod.render();
    }
}