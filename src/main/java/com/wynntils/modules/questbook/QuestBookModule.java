/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.events.ClientEvents;
import com.wynntils.modules.questbook.instances.MainPage;
import com.wynntils.modules.questbook.instances.QuestsPage;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.modules.questbook.overlays.hud.TrackedQuestOverlay;
import com.wynntils.modules.questbook.overlays.ui.QuestBookGUIold;
import com.wynntils.modules.questbook.managers.QuestBookHandler;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "quest_book", displayName = "Quest Book")
public class QuestBookModule extends Module {

    public static final QuestBookGUIold gui = new QuestBookGUIold();

    public void onEnable() {
        registerEvents(new ClientEvents());

        registerSettings(QuestBookConfig.class);
        registerOverlay(new TrackedQuestOverlay(), Priority.HIGHEST);

        QuestBookHandler.registerPage("MainPage", new MainPage());
        QuestBookHandler.registerPage("QuestsPage", new QuestsPage());

        registerKeyBinding("Open Quest Book", Keyboard.KEY_K, "Wynntils", true, () -> {
            QuestManager.requestQuestBookReading();
            gui.openAtQuests();
        });
        registerKeyBinding("Open Item Guide", Keyboard.KEY_I, "Wynntils", true, gui::openAtItemGuide);
    }

}
