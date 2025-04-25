package dev.neuxs.fullbright;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import finalforeach.cosmicreach.GameSingletons;
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
    public static boolean isFullbrightEnabled = false;

    public static void init() {
        LOGGER.info("{} v{} Initializing...", MOD_NAME, VERSION);
        LOGGER.info("{} v{} Initialized!", MOD_NAME, VERSION);
    }

    public static void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) toggleFullbright();
    }

    public static void toggleFullbright() {
        isFullbrightEnabled = !isFullbrightEnabled;
        if (isFullbrightEnabled) enableFullbright();
        else disableFullbright();
    }

    public static void enableFullbright() {
        try {
            ChunkShader customChunkShader = new ChunkShader(
                    Identifier.of(MOD_ID, "shaders/chunk.vert.glsl"),
                    Identifier.of(MOD_ID, "shaders/chunk.frag.glsl")
            );
            ChunkShader customWaterShader = new ChunkShader(
                    Identifier.of(MOD_ID, "shaders/chunk-water.vert.glsl"),
                    Identifier.of(MOD_ID, "shaders/chunk-water.frag.glsl")
            );
            ChunkShader.DEFAULT_BLOCK_SHADER = customChunkShader;
            ChunkShader.WATER_BLOCK_SHADER = customWaterShader;

            reloadChunks();

            isFullbrightEnabled = true;
        } catch (Exception e) {
            LOGGER.error("Something went wrong enabling Fullbright: {}", e.getMessage(), e);
        }
    }

    public static void disableFullbright() {
        try {
            ChunkShader.initChunkShaders();
            reloadChunks();
            isFullbrightEnabled = false;
        } catch (Exception e) {
            LOGGER.error("Something went wrong disabling Fullbright: {}", e.getMessage(), e);
        }
    }

    private static void reloadChunks() {
        World world = InGame.getWorld();

        GameShader.reloadAllShaders();

        for (Zone zone : world.getZones()) {
            for (Region region : zone.getRegions()) {
                for (Chunk chunk : region.getChunks()) {
                    if (chunk.getMeshGroup() != null) {
                        chunk.getMeshGroup().dispose();
                    }
                    chunk.setMeshGroup(null);
                    GameSingletons.zoneRenderer.addChunk(chunk);
                    chunk.flagForRemeshing(true);
                }
            }
        }

        GameSingletons.meshGenThread.meshChunks(GameSingletons.zoneRenderer);
    }
}
