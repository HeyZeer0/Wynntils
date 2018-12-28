package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "overlays", displayPath = "Overlays")
public class OverlayConfig extends SettingsClass {
    public static OverlayConfig INSTANCE;


    @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
    public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Setting(displayName = "Action Bar Overwrite Coordinates", description = "Should the coordinates be overwritten by the action bar?")
    public boolean overwrite = true;


    @SettingsInfo(name = "health_settings", displayPath = "Overlays/Health")
    public static class Health extends SettingsClass {
        public static Health INSTANCE;

        @Setting(displayName = "Health Texture", description = "What texture should be used for the health bar?")
        public HealthTextures healthTexture = HealthTextures.a;

        @Setting(displayName = "Enabled", description = "Should the health bar be displayed?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played? (0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum HealthTextures {
            Wynn,
            a,
            b,
            c,
            d
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }


    @SettingsInfo(name = "mana_settings", displayPath = "Overlays/Mana")
    public static class Mana extends SettingsClass {
        public static Mana INSTANCE;

        @Setting(displayName = "Mana Texture", description = "What texture should be used for the mana bar?")
        public ManaTextures manaTexture = ManaTextures.a;

        @Setting(displayName = "Enabled", description = "Should the mana bar be displayed?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played? (0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum ManaTextures {
            Wynn,
            a,
            b,
            c,
            d
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }

    @SettingsInfo(name = "exp_settings", displayPath = "Overlays/Experience")
    public static class Exp extends SettingsClass {
        public static Exp INSTANCE;

        @Setting(displayName = "EXP Texture", description = "What texture should be used for the EXP bar?")
        public expTextures expTexture = expTextures.a;

        @Setting(displayName = "Enabled", description = "Should the EXP bar be displayed?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played? (0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum expTextures {
            Wynn,
            a,
            b,
            c
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }

    @SettingsInfo(name = "bubbles_settings", displayPath = "Overlays/Bubbles")
    public static class Bubbles extends SettingsClass {
        public static Bubbles INSTANCE;

        @Setting(displayName = "Bubbles Texture", description = "What texture should be used for the EXP bar when it acts as the air meter?")
        public BubbleTexture bubblesTexture = BubbleTexture.a;

        @Setting(displayName = "Enabled", description = "Should the EXP bar act as the air meter when underwater?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played? (0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "Bubble Vignette", description = "Should the drowning vignette be displayed?")
        public boolean drowningVignette = true;

        public enum BubbleTexture {
            Wynn,
            a,
            b,
            c
        }
    }


    @SettingsInfo(name = "leveling_settings", displayPath = "Overlays/Leveling")
    public static class Leveling extends SettingsClass {
        public static Leveling INSTANCE;

        @Setting.Features.StringParameters(parameters = {"actual", "max", "percent"})
        @Setting(displayName = "Current Text", description = "How should the leveling text be displayed?")
        public String levelingText = "§a(%actual%/%max%) §6%percent%%";

        @Setting(displayName = "Enabled", description = "Should the level bar be displayed?")
        public boolean enabled = true;

        @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


    }

    @SettingsInfo(name = "game_update_settings", displayPath = "Overlays/Update Ticker")
    public static class GameUpdate extends SettingsClass {
        public static GameUpdate INSTANCE;

        // Default settings designed for large gui scale @ 1080p
        // I personally use gui scale normal - but this works fine with that too

        @Setting(displayName = "Message Limit", description = "What should the maximum amount of ticker messages displayed in the game-update-list be?")
        @Setting.Limitations.IntLimit(min = 1, max = 20)
        public int messageLimit = 5;

        @Setting(displayName = "Message Expiry Time", description = "How long (in seconds) should a ticker message remain on the screen?")
        @Setting.Limitations.FloatLimit(min = 0.2f, max = 20f, precision = 0.2f)
        public float messageTimeLimit = 10f;

        @Setting(displayName = "Message Fadeout Animation", description = "How long should the fadeout animation be played?")
        @Setting.Limitations.FloatLimit(min = 10f, max = 60f, precision = 1f)
        public float messageFadeOut = 30f;

        @Setting(displayName = "Invert Growth", description = "Should the way ticker messages grow be inverted?")
        public boolean invertGrowth = true;

        @Setting(displayName = "Enabled", description = "Should the game-update-ticker be displayed?")
        public boolean enabled = true;

        @Setting(displayName = "Offset X", description = "How far should the ticker be offset on the x-axis?")
        @Setting.Limitations.IntLimit(min = -300, max = 10)
        public int offsetX = 0;

        @Setting(displayName = "Offset Y", description = "How far should the ticker be offset on the y-axis?")
        @Setting.Limitations.IntLimit(min = -300, max = 10)
        public int offsetY = -70;

        @Setting(displayName = "Max Message Length", description = "What should the maximum length of messages in the game-update-ticker be? Messages longer than this set value will be truncated. (0 = unlimited)")
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int messageMaxLength = 0;

        @Setting(displayName = "Text Shadow", description = "The text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "New Message Override", description = "Should new messages force out the oldest previous messages? If disabled, ticker messages will be queued and appear when a previous message disappears.")
        public boolean overrideNewMessages = true;

        @SettingsInfo(name = "game_update_exp_settings", displayPath = "Overlays/Update Ticker/Experience")
        public static class GameUpdateEXPMessages extends SettingsClass {
            public static GameUpdateEXPMessages INSTANCE;

            @Setting(displayName = "Enable EXP Messages", description = "Should EXP messages be displayed in the game-update-ticker?")
            public boolean enabled = true;

            @Setting(displayName = "EXP Message Update Rate", description = "How often should the EXP change messages (in seconds) be added to the game update ticker?")
            @Setting.Limitations.FloatLimit(min = 0.2f, max = 10f, precision = 0.2f)
            public float expUpdateRate = 1f;

            @Setting(displayName = "EXP Message Format", description = "How should the format of EXP messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"xo", "xn", "xc", "po", "pn", "pc"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String expMessageFormat = "§2+%xc%XP (§6+%pc%%§2)";
        }

        @SettingsInfo(name = "game_update_redirect_settings", displayPath = "Overlays/Update Ticker/Redirect Messages")
        public static class RedirectSystemMessages extends SettingsClass {
            public static RedirectSystemMessages INSTANCE;

            @Setting(displayName = "Redirect Combat Messages", description = "Should combat chat messages be redirected to the game update ticker?")
            public boolean redirectCombat = true;

            @Setting(displayName = "Redirect Horse Messages", description = "Should messages related to your horse be redirected to the game update ticker?")
            public boolean redirectHorse = true;

            @Setting(displayName = "Redirect Local Login Messages", description = "Should local login messages (for people with ranks) be redirected to the game update ticker?")
            public boolean redirectLoginLocal = true;

            @Setting(displayName = "Redirect Friend Login Messages", description = "Should login messages for friends be redirected to the game update ticker?")
            public boolean redirectLoginFriend = true;

            @Setting(displayName = "Redirect Merchant Messages", description = "Should item buyer and identifier messages be redirected to the game update ticker?")
            public boolean redirectMerchants = true;

            @Setting(displayName = "Redirect Other Messages", description = "Should skill points, price of identifying items, and other users' level up messages be redirected to the game update ticker?")
            public boolean redirectOther = true;

            @Setting(displayName = "Redirect Server Status", description = "Should server shutdown messages be redirected to the game update ticker?")
            public boolean redirectServer = true;

            @Setting(displayName = "Redirect Quest Messages", description = "Should messages relating to the progress of a quest be redirected to the game update ticker?")
            public boolean redirectQuest = true;
        }

        @SettingsInfo(name = "game_update_territory_settings", displayPath = "Overlays/Update Ticker/Territory Change")
        public static class TerritoryChangeMessages extends SettingsClass {
            public static TerritoryChangeMessages INSTANCE;

            @Setting(displayName = "Enable Territory Change", description = "Should territory change messages be displayed in the game update ticker?")
            public boolean enabled = false;

            @Setting(displayName = "Enable Territory Enter", description = "Should territory enter messages be displayed in the game update ticker?")
            public boolean enter = true;

            @Setting(displayName = "Enable Territory Leave", description = "Should territory leave messages be displayed in the game update ticker?")
            public boolean leave = false;

            @Setting(displayName = "Enable Music Change", description = "Should music change messages be displayed in the game update ticker? (This has no effect if the music module is disabled)")
            public boolean musicChange = true;

            @Setting(displayName = "Territory Enter Format", description = "How should the format of the territory enter ticker messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryEnterFormat = "§7Now Entering [%t%]";

            @Setting(displayName = "Territory Leave Format", description = "How should the format of the territory leave ticker messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryLeaveFormat = "§7Now Leaving [%t%]";

            @Setting(displayName = "Music Change Format", description = "How should the format of the music change ticker messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"np"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String musicChangeFormat = "§7♫ %np%";
        }
    }
    
    @SettingsInfo(name = "war_timer_settings", displayPath = "Overlays/War Timer")
    public static class WarTimer extends SettingsClass {
        public static WarTimer INSTANCE;
        
        @Setting(displayName = "Enabled", description = "Should a timer of when a war will start be displayed?")
        public boolean enabled = true;
        
        @Setting(displayName = "Text Shadow", description = "The HUD text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }
}
