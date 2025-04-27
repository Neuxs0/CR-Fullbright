package dev.neuxs.fullbright;

import com.badlogic.gdx.Gdx;
import dev.neuxs.fullbright.settings.SettingsManager;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Mod {
    public static final String MOD_ID = "fullbright";
    public static final String MOD_NAME = "Fullbright";
    public static final String VERSION = "1.0.1";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static SettingsManager settingsManager;
    public static boolean isFullbrightEnabled = false;
    public static boolean isNoFogEnabled = false;

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
            ChunkShader customChunkShader;
            ChunkShader customWaterShader;

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

        Method getChunksMethod;
        try {
            getChunksMethod = Region.class.getMethod("getChunks");
        } catch (NoSuchMethodException e) {
            LOGGER.error("Reflection failed: Region#getChunks() not found", e);
            return;
        }

        for (Zone zone : world.getZones()) {
            if (zone == null) continue;
            Region[] regions = zone.getRegions();
            if (regions == null) continue;
            for (Region region : regions) {
                if (region == null) continue;
                try {
                    Object rawChunks = getChunksMethod.invoke(region);
                    if (rawChunks == null) continue;

                    Chunk[] chunksArray;
                    int count;

                    if (rawChunks instanceof Chunk[]) {
                        chunksArray = (Chunk[]) rawChunks;
                        count = chunksArray.length;
                    } else {
                        Class<?> containerClass = rawChunks.getClass();
                        Field itemsField = containerClass.getField("items");
                        Field sizeField  = containerClass.getField("size");

                        chunksArray = (Chunk[]) itemsField.get(rawChunks);
                        count = sizeField.getInt(rawChunks);
                    }

                    for (int i = 0; i < count; i++) {
                        Chunk chunk = chunksArray[i];
                        if (chunk == null) continue;

                        if (chunk.getMeshGroup() != null) chunk.getMeshGroup().dispose();
                        chunk.setMeshGroup(null);

                        if (GameSingletons.zoneRenderer != null) GameSingletons.zoneRenderer.addChunk(chunk);
                        chunk.flagForRemeshing(false);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                    LOGGER.error("Error remeshing region via reflection: {}", e.getMessage(), e);
                }
            }
        }
    }
}
