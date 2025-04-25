package dev.neuxs.fullbright;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod {
    public static final String MOD_ID = "fullbright";
    public static final String MOD_NAME = "Fullbright";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init(String type) {
        LOGGER.info("{} v{} Initializing with {}...", MOD_NAME, VERSION, type);
        LOGGER.info("{} v{} Initialized!", MOD_NAME, VERSION);
    }
}
