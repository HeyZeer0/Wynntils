package cf.wynntils.modules.chat.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;
import cf.wynntils.modules.chat.instances.ChatTab;
import cf.wynntils.modules.chat.managers.ChatManager;
import cf.wynntils.modules.chat.managers.TabManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SettingsInfo(name = "chat", displayPath = "Chat")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps attached before the beginning of them?")
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Transparent Chat", description = "Should the chat window be transparent?")
    public boolean transparent = false;

    @Setting(displayName = "Chat Timestamp Format", description = "How should the timestamps be displayed? (This has no effect if chat timestamps are disabled.)")
    public String timestampFormat = "HH:mm:ss";

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username appears in chat?")
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack rather than flood the chat?")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Filter Info Messages", description = "Should Wynncraft Info messages be filtered? (Messages starting with §4[Info]§f will no longer appear in chat.)")
    public boolean filterWynncraftInfo = true;

    public boolean registeredDefaultTabs = false;

    public ArrayList<ChatTab> available_tabs = new ArrayList<>();

    @Setting(displayName = "Alter chat tab by presets", description = "Select a pre-made selection of tabs to be set.\n\na - Global, Guild, Party\nb - Global, Shouts, Guild/Party, PM's\nvanilla - All")
    public Presets preset = Presets.a;

    public enum Presets {
        a,
        b,
        vanilla
    }

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("timestampFormat")) {
            try {
                ChatManager.dateFormat = new SimpleDateFormat(timestampFormat);
                ChatManager.validDateFormat = true;
            } catch (IllegalArgumentException ex) {
                ChatManager.validDateFormat = false;
            }
        } else if (name.equals("preset")) {
            TabManager.registerPresets();
        }
    }

}
