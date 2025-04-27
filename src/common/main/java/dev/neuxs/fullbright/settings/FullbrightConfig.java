package dev.neuxs.fullbright.settings;

import java.util.Objects;

public class FullbrightConfig {

    private static final int DEFAULT_KEYBIND = 34;
    private static final boolean DEFAULT_ENABLED = false;

    private int keybind;
    private boolean enabled;

    public FullbrightConfig() {
        this(DEFAULT_KEYBIND, DEFAULT_ENABLED);
    }

    public FullbrightConfig(int keybind, boolean enabled) {
        this.keybind = keybind;
        this.enabled = enabled;
    }

    public int getKeybind() {
        return keybind;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean setKeybind(int keybind) {
        if (this.keybind != keybind) {
            this.keybind = keybind;
            return true;
        }
        return false;
    }

    public boolean setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullbrightConfig that = (FullbrightConfig) o;
        return keybind == that.keybind && enabled == that.enabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keybind, enabled);
    }

    @Override
    public String toString() {
        return "FullbrightConfig{" +
                "keybind=" + keybind +
                ", enabled=" + enabled +
                '}';
    }
}