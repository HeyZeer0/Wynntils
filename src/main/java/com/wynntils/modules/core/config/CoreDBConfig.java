/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.core.config;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.modules.core.enums.ScrollDirection;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

@SettingsInfo(name = "main", displayPath = "Core")
public class CoreDBConfig extends SettingsClass {
    public static CoreDBConfig INSTANCE;

    @Setting(upload = false)
    public String lastToken = "";

    @Setting(upload = false)
    public int lastSelectedClass = -1;

    @Setting(upload = false)
    public ClassType lastClass = ClassType.NONE;

    @Setting(upload = false)
    public boolean lastClassIsReskinned = false;

    @Setting(displayName = "Update Stream", description = "Which update stream should the mod be on?\n\n" +
            "§2Stable: §rThe mod will only update when a new version is released. Stable versions are generally more stable than Cutting Edge builds.\n\n" +
            "§4Cutting Edge: §rThe mod will update whenever a new build is released. Cutting Edge builds will include features that are not yet in Stable versions and are currently in development but may also be less stable than Stable versions.", upload = false)
    public UpdateStream updateStream = UpdateStream.STABLE;

    @Setting(displayName="Automatic Update Checks", description = "Should Wynntils automatically check for mod updates?", upload = false)
    public boolean autoCheckUpdates = true;

    @Setting(displayName = "Scroll Direction", description = "Which direction should your mouse scroll for the page to scroll down?")
    public ScrollDirection scrollDirection = ScrollDirection.DOWN;

    @Setting(displayName = "Show Changelog", description = "After updating Wynntils, should a changelog be displayed upon logging in?")
    public boolean enableChangelogOnUpdate = true;

    @Setting(upload = false)
    public boolean showChangelogs = true;

    @Setting(upload = false)
    public String lastVersion = "0.0.0";

    @Setting(displayName = "Main Menu Wynncraft Button", description = "Should a button be added to the main menu that allows you to connect to Wynncraft directly?")
    public boolean addMainMenuButton = true;

    @Setting(displayName = "Use Unicode Font", description = "Should Wynntils use the unicode font?")
    public boolean useUnicode = false;

    @Override
    public void onSettingChanged(String name) {
        // don't want to check for updates too early in the init process, since the config might not have finished
        // loading yet and we want to respect the "autoCheckUpdates" option
        if (name.equals("updateStream") && Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
            WebManager.checkForUpdates(true);
        }
    }
}
