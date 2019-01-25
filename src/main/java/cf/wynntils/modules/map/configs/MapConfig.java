package cf.wynntils.modules.map.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Enable minimap", description = "Should a minimap be displayed?")
    public boolean enabled = true;

    @Setting(displayName = "Map Format", description = "Should the Map be a Square or a Circle?")
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Follow Player Rotation", description = "Should the Map follow the Player's Rotation?")
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Map Size", description = "How large should the Map be?")
    @Setting.Limitations.IntLimit(min = 75, max = 200)
    public int mapSize = 100;

    @Setting(displayName = "Map Zoom", description = "How far zoomed in should the map be?")
    @Setting.Limitations.IntLimit(min = 0, max = 100, precision = 5)
    public int mapZoom = 30;

    @Setting(displayName = "Texture Style", description = "What should the Map Texture be?")
    public TextureType textureType = TextureType.Paper;

    public enum MapFormat {
        SQUARE, CIRCLE
    }

    public enum TextureType {
        Paper, Wynn
    }

    @Override
    public void onSettingChanged(String name) { }

}
