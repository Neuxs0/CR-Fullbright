# Fullbright
Removes darkness and fog to let you see anywhere

## Dependencies:
- Puzzle Loader any version or Cosmic Quilt any version
- Cosmic Reach v0.3.2 or newer

## How to use
- Run the mod as any other Quilt/Puzzle mod
- Press `f` to toggle fullbright (Configurable in `/your-cosmic-reach-folder/config/fullbright.json`)
- If you want no fog alongside fullbright, you can enable it by going into `/your-cosmic-reach-folder/config/fullbright.json` then changing `"nofog": false` to `"nofog": true`

## How to build
Requirements:
- JDK 17 (You can install it [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).)
- python 3 (You can install it [here](https://www.python.org/downloads/).)

1. (Optional) Configure `build_config.json`
2. Run `python3 build.py` and wait for the build script to finish
3. The jar will be in the newly generated `dist/` folder
