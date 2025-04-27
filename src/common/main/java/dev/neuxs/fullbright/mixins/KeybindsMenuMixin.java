package dev.neuxs.fullbright.mixins;

import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.KeybindsMenu;
import finalforeach.cosmicreach.settings.Keybind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "SameParameterValue"})
@Mixin(KeybindsMenu.class)
public abstract class KeybindsMenuMixin {

    @Shadow
    protected abstract void addKeybindButton(String label, Keybind keybind);

    // Need the game to use a different keybind button format so the button does not go off of the screen
    @Inject(method = "<init>(Lfinalforeach/cosmicreach/gamestates/GameState;)V", at = @At("TAIL"))
    private void addFullbrightButton(GameState previousState, CallbackInfo ci) {
//        this.addKeybindButton("Fullbright", SettingsManager.getInstance().getKeybind());
    }
}