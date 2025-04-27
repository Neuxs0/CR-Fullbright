package dev.neuxs.fullbright;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.rendering.shaders.ChunkShader;
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
    public static boolean isFullbrightEnabled = false;
    public static boolean isConfigEnabled = false;

    private static ChunkShader customChunkShader = null;
    private static ChunkShader customWaterShader = null;

    public static void init() {
        LOGGER.info("{} v{} Initializing...", MOD_NAME, VERSION);
        LOGGER.info("{} v{} Initialized!", MOD_NAME, VERSION);
    }

    public static void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F) || (!isFullbrightEnabled && isConfigEnabled)) toggleFullbright();
    }

    public static void toggleFullbright() {
        isFullbrightEnabled = !isFullbrightEnabled;
        if (isFullbrightEnabled) enableFullbright();
        else disableFullbright();
    }

    public static void enableFullbright() {
        try {
            if (customChunkShader == null) {
                customChunkShader = new ChunkShader(
                        Identifier.of(MOD_ID, "shaders/chunk.vert.glsl"),
                        Identifier.of(MOD_ID, "shaders/chunk.frag.glsl")
                );
            }
            if (customWaterShader == null) {
                customWaterShader = new ChunkShader(
                        Identifier.of(MOD_ID, "shaders/chunk-water.vert.glsl"),
                        Identifier.of(MOD_ID, "shaders/chunk-water.frag.glsl")
                );
            }

            ChunkShader.DEFAULT_BLOCK_SHADER = customChunkShader;
            ChunkShader.WATER_BLOCK_SHADER = customWaterShader;
            reloadChunks();
        } catch (Exception e) {
            LOGGER.error("Something went wrong enabling Fullbright: {}", e.getMessage(), e);
            isFullbrightEnabled = false;
        }
    }

    public static void disableFullbright() {
        try {
            ChunkShader.initChunkShaders();
            reloadChunks();
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
