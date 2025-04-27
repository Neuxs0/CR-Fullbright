package dev.neuxs.fullbright;

import com.badlogic.gdx.Gdx;
import dev.neuxs.fullbright.settings.SettingsManager;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.rendering.shaders.ChunkShader;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Region;
import finalforeach.cosmicreach.world.World;
import finalforeach.cosmicreach.world.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod {
    public static final String MOD_ID = "fullbright";
    public static final String MOD_NAME = "Fullbright";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static SettingsManager settingsManager;
    public static boolean isFullbrightEnabled = false;
    public static boolean isNoFogEnabled = false;

    private static ChunkShader customChunkShader = null;
    private static ChunkShader customWaterShader = null;

    public static void init() {
        LOGGER.info("{} v{} Initializing...", MOD_NAME, VERSION);
        settingsManager = SettingsManager.getInstance();
        LOGGER.info("{} v{} Initialized!", MOD_NAME, VERSION);
    }

    public static void render() {
        if (Gdx.input.isKeyJustPressed(settingsManager.getKeybind())
                || (isFullbrightEnabled != settingsManager.isEnabled())) toggleFullbright();
        if (isFullbrightEnabled && isNoFogEnabled != settingsManager.isNofog()) {
            isNoFogEnabled = !isNoFogEnabled;
            enableFullbright();
        }
    }

    public static void toggleFullbright() {
        isFullbrightEnabled = !isFullbrightEnabled;
        if (settingsManager.isNofog()) isNoFogEnabled = !isNoFogEnabled;
        if (isFullbrightEnabled) enableFullbright();
        else disableFullbright();
    }

    public static void enableFullbright() {
        try {
            if (isNoFogEnabled) {
                customChunkShader = new ChunkShader(
                        Identifier.of(MOD_ID, "shaders/fullbright-nofog/chunk.vert.glsl"),
                        Identifier.of(MOD_ID, "shaders/fullbright-nofog/chunk.frag.glsl")
                );
                customWaterShader = new ChunkShader(
                        Identifier.of(MOD_ID, "shaders/fullbright-nofog/chunk-water.vert.glsl"),
                        Identifier.of(MOD_ID, "shaders/fullbright-nofog/chunk-water.frag.glsl")
                );
            } else {
                customChunkShader = new ChunkShader(
                        Identifier.of(MOD_ID, "shaders/fullbright/chunk.vert.glsl"),
                        Identifier.of(MOD_ID, "shaders/fullbright/chunk.frag.glsl")
                );
                customWaterShader = new ChunkShader(
                        Identifier.of(MOD_ID, "shaders/fullbright/chunk-water.vert.glsl"),
                        Identifier.of(MOD_ID, "shaders/fullbright/chunk-water.frag.glsl")
                );
            }

            ChunkShader.DEFAULT_BLOCK_SHADER = customChunkShader;
            ChunkShader.WATER_BLOCK_SHADER = customWaterShader;
            reloadChunks();
            GameShader.reloadAllShaders();
            settingsManager.setEnabled(true);
        } catch (Exception e) {
            LOGGER.error("Something went wrong enabling Fullbright: {}", e.getMessage(), e);
            settingsManager.setEnabled(false);
            isFullbrightEnabled = false;
        }
    }

    public static void disableFullbright() {
        try {
            ChunkShader.initChunkShaders();
            reloadChunks();
            GameShader.reloadAllShaders();
            settingsManager.setEnabled(false);
            isFullbrightEnabled = false;
        } catch (Exception e) {
            LOGGER.error("Something went wrong disabling Fullbright: {}", e.getMessage(), e);
        }
    }

    private static void reloadChunks() {
        World world = InGame.getWorld();
        if (world == null) return;

        for (Zone zone : world.getZones()) {
            if (zone == null) continue;
            Region[] regions = zone.getRegions();
            if (regions == null) continue;
            for (Region region : regions) {
                if (region == null) continue;
                Chunk[] chunks = region.getChunks().items;
                if (chunks == null) continue;
                for (int i = 0; i < region.getChunks().size; i++) {
                    Chunk chunk = chunks[i];
                    if (chunk == null) continue;
                    if (chunk.getMeshGroup() != null) chunk.getMeshGroup().dispose();
                    chunk.setMeshGroup(null);
                    if (GameSingletons.zoneRenderer != null) GameSingletons.zoneRenderer.addChunk(chunk);
                    chunk.flagForRemeshing(false);
                }
            }
        }
    }
}
