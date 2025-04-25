package dev.neuxs.fullbright;

import com.github.puzzle.core.loader.launch.provider.mod.entrypoint.impls.ClientModInitializer;

import dev.neuxs.fullbright.Mod;

public class PuzzleMod implements ClientModInitializer {
    @Override
    public void onInit() {
        Mod.init("Puzzle Loader");
    }
}