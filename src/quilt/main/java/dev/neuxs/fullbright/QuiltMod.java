package dev.neuxs.fullbright;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;

import org.quiltmc.loader.api.ModContainer;

public class QuiltMod implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		Mod.init();
	}
}
