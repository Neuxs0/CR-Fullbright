package dev.neuxs.fullbright.settings;

import java.util.Objects;

public class FullbrightConfig {

    private static final int DEFAULT_KEYBIND = 34;
    private static final boolean DEFAULT_ENABLED = false;
    private static final boolean DEFAULT_NOFOG = false;

    private final int keybind;
    private boolean enabled;
    private boolean nofog;

    public FullbrightConfig() {
        this(DEFAULT_KEYBIND, DEFAULT_ENABLED, DEFAULT_NOFOG);
    }

    public FullbrightConfig(int keybind, boolean enabled, boolean nofog) {
        this.keybind = keybind;
        this.enabled = enabled;
        this.nofog = nofog;
    }

    public int getKeybind() {
        return keybind;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            return true;
        }
        return false;
    }

    public boolean isNofog() {
        return nofog;
    }

    public boolean setNofog(boolean nofog) {
        if (this.nofog != nofog) {
            this.nofog = nofog;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullbrightConfig that = (FullbrightConfig) o;
        return keybind == that.keybind && enabled == that.enabled && nofog == that.nofog;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keybind, enabled, nofog);
    }

    @Override
    public String toString() {
        return "FullbrightConfig{" +
                "keybind=" + keybind +
                ", enabled=" + enabled +
                ", nofog=" + nofog +
                '}';
    }

    public static boolean getDefaultNofog() {
        return DEFAULT_NOFOG;
    }

    public static int getDefaultKeybind() {
        return DEFAULT_KEYBIND;
    }

    public static boolean getDefaultEnabled() {
        return DEFAULT_ENABLED;
    }
}
