/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.utilities.events.ServerEvents;
import com.wynntils.modules.utilities.managers.WindowIconManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;

import java.util.HashMap;
import java.util.HashSet;

@SettingsInfo(name = "main", displayPath = "Utilities")
public class UtilitiesConfig extends SettingsClass {
    public static UtilitiesConfig INSTANCE;

    @Setting(displayName = "Add Class & Hub Buttons to Menu", description = "Should 'Class selection' and 'Back to Hub' buttons be displayed on the in-game menu?", order = 1)
    public boolean addClassHubButtons = true;

    @Setting(displayName = "Add Options & Profile Buttons to Menu", description = "Should 'Wynntils Option' and 'User Profile'  buttons be displayed on the in-game menu?", order = 2)
    public boolean addOptionsProfileButtons = true;

    @Setting(displayName = "Daily Chest Reminder", description = "Should a message notifying that you can claim your daily chest be sent upon joining a world?")
    public boolean dailyReminder = true;

    @Setting(displayName = "Block Player Collision When AFK", description = "Should player collision be blocked when you are AFK?")
    public boolean blockAfkPushs = true;

    @Setting(displayName = "AFK Protection", description = "Should you enter class selection when you are AFK?")
    public boolean afkProtection = false;

    @Setting.Limitations.FloatLimit(min = 1f, max = 30f)
    @Setting(displayName = "AFK Threshold", description = "How many minutes of inactivity is required for AFK Protection.")
    public float afkProtectionThreshold = 10f;

    @Setting(displayName = "Hide Vanilla Active Potions Indicators", description = "Should the indicator for active potion effects (black squares) be hidden?")
    public boolean hidePotionGui = true;

    @Setting(displayName = "Hide Nametags Through Walls", description = "Should nametags be hidden when behind opaque blocks?")
    public boolean hideNametags = true;

    @Setting(displayName = "Hide Nametags' Box", description = "Should the box around nametags be hidden?")
    public boolean hideNametagBox = true;

    @Setting(displayName = "Show Players' Armour", description = "Should the worn armour of players be listed underneath their nametag?\n\n§8Crafted armour cannot be displayed.")
    public boolean showArmors = false;

    @Setting(displayName = "Prevent Mythic Loot Chest Closing", description = "Should the closing of loot chests be prevented when they contain mythics?")
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "Prevent Slot Click on Locked Items", description = "Should moving items to and from locked inventory slots be blocked?")
    public boolean preventSlotClicking = false;

    @Setting(displayName = "Disable FOV Changes with Speed Effect", description = "Should your FOV remain unchanged when you have speed?")
    public boolean disableFovChanges = false;

    @Setting(displayName = "Auto Mount Horse", description = "Should you mount your horse automatically when it is spawned?")
    public boolean autoMount = false;

    @Setting(displayName = "Block Health Potions When at Full Health", description = "Should the mod prevent you from using your health potions when you are at full health?")
    public boolean blockHealingPots = true;

    @Setting(displayName = "Apply Wynncraft Resource Pack", description = "Should the Wynncraft server resource pack be applied when joining the server instead of when picking your class?")
    public boolean autoResource = true;

    @Setting(displayName = "Apply Wynncraft Resource Pack During Startup", description = "Should the Wynncraft server resource pack be applied when starting Minecraft?")
    public boolean autoResourceOnLoad = false;

    @Setting(displayName = "Display Confirmation for Buying Bank Pages", description = "Should Wynntils display a GUI confirmation when buying bank pages?")
    public boolean addBankConfirmation = true;

    @Setting(displayName = "Open Chat Bank Search", description = "Should the chat open when the bank search asks you to type a response?")
    public boolean openChatBankSearch = true;

    @Setting(displayName = "Show Item Identification Stars", description = "Should the star rating of an item's identification be shown?")
    public boolean addItemIdentificationStars = false;

    @Setting(displayName = "Categorize Item Identifications", description = "Should the identifications in an item's tooltip be categorized?")
    public boolean addItemIdentificationSpacing = true;

    @Setting(displayName = "Categorize Set Bonus Identifications", description = "Should the set bonus in an item's tooltip be categorized?")
    public boolean addSetBonusSpacing = true;

    @Setting(displayName = "Indicate Newly Added Items", description = "Should the mod append a \"NEW\" tag to the name of items that have recently been added to the game?")
    public boolean showNewItems = false;

    @Setting(displayName = "Change Window Title", description = "Should the mod change the window title to \"Wynncraft\" while on the server?")
    public boolean changeWindowTitle = true;

    @Setting(displayName = "Change Window Icon", description = "Should the mod change the window icon to the Wynncraft logo while on the server?\n\nThis does not work on macOS systems")
    public boolean changeWindowIcon = true;

    @Setting(displayName = "Show Tooltips From Top", description = "Should tooltips be rendered from the top by default?")
    public boolean renderTooltipsFromTop = true;

    @Setting(displayName = "Scale Tooltips", description = "Should tooltips be scaled down so that they fit on your screen?")
    public boolean renderTooltipsScaled = false;

    @Setting(displayName = "Show Leaderboard Badges", description = "Should leaderboard members have a badge over their heads?")
    public boolean renderLeaderboardBadges = true;

    @Setting(upload = false)
    public String lastServerResourcePack = "";

    @Setting(upload = false)
    public String lastServerResourcePackHash = "";

    @Setting
    public HashMap<Integer, HashSet<Integer>> locked_slots = new HashMap<>();

    @SettingsInfo(name = "wars", displayPath = "Utilities/Wars")
    public static class Wars extends SettingsClass {
        public static Wars INSTANCE;

        @Setting(displayName = "Entity Filter", description = "Should the nametag of mobs be hidden in war servers?")
        public boolean allowEntityFilter = true;

        @Setting(displayName = "Block Workstations", description = "Should the mod prevent you from clicking on workstations while in war servers?")
        public boolean blockWorkstations = true;

        @Setting(displayName = "Show Player Health Bar", description = "Should the health bar of other players in a war server be displayed above their heads?")
        public boolean warrerHealthBar = true;

    }

    @SettingsInfo(name = "data", displayPath = "")
    public static class Data extends SettingsClass {
        public static Data INSTANCE;

        @Setting
        public long dailyReminder = 0L;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "item_highlights", displayPath = "Utilities/Item Highlights")
    public static class Items extends SettingsClass {
        public static Items INSTANCE;

        @Setting(displayName = "Filter for Ingredients", description = "Should a filter for ingredients and crafted items be displayed?", order = 0)
        public boolean filterEnabled = true;

        @Setting(displayName = "Item Highlights in Containers", description = "Should items be highlighted according to rarity in remote containers? (chests, bank, etc.)", order = 11)
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item Highlights in Inventory", description = "Should items be highlighted according to rarity in your inventory?", order = 12)
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "Accessories Highlight", description = "Should your worn accessories be highlighted according to rarity?", order = 13)
        public boolean accesoryHighlight = true;

        @Setting(displayName = "Highlight Hotbar Items", description = "Should the items in your hotbar be highlighted according to rarity?", order = 14)
        public boolean hotbarHighlight = true;

        @Setting(displayName = "Highlight Armour Items", description = "Should your worn armour be highlighted according to rarity?", order = 15)
        public boolean armorHighlight = true;

        @Setting(displayName = "Highlight Mythics", description = "Should mythic items be highlighted?", order = 20)
        public boolean mythicHighlight = true;

        @Setting(displayName = "Highlight Fabled", description = "Should fabled items be highlighted?", order = 21)
        public boolean fabledHighlight = true;

        @Setting(displayName = "Highlight Legendaries", description = "Should legendary items be highlighted?", order = 22)
        public boolean legendaryHighlight = true;

        @Setting(displayName = "Highlight Rares", description = "Should rare items be highlighted?", order = 23)
        public boolean rareHighlight = true;

        @Setting(displayName = "Highlight Uniques", description = "Should unique items be highlighted?", order = 24)
        public boolean uniqueHighlight = true;

        @Setting(displayName = "Highlight Set Items", description = "Should set items be highlighted?", order = 25)
        public boolean setHighlight = true;

        @Setting(displayName = "Highlight Normal Items", description = "Should normal items be highlighted?", order = 26)
        public boolean normalHighlight = false;

        @Setting(displayName = "Highlight Black Market Cosmetics", description = "Should black market cosmetic items be highlighted?", order = 30)
        public boolean blackMarketEffectsHighlight = true;

        @Setting(displayName = "Highlight Godly Cosmetics", description = "Should godly cosmetic items be highlighted?", order = 31)
        public boolean godlyEffectsHighlight = true;

        @Setting(displayName = "Highlight Epic Cosmetics", description = "Should epic cosmetic items be highlighted?", order = 32)
        public boolean epicEffectsHighlight = true;

        @Setting(displayName = "Highlight Rare Cosmetics", description = "Should rare cosmetic items be highlighted?", order = 33)
        public boolean rareEffectsHighlight = true;

        @Setting(displayName = "Highlight Common Cosmetics", description = "Should common cosmetic items be highlighted?", order = 34)
        public boolean commonEffectsHighlight = true;

        // TODO: move these 3 configs
        @Setting(displayName = "Show Emerald Count in Containers", description = "Should your emerald count be displayed in remote containers? (chests, bank, etc.)", order = 101)
        public boolean emeraldCountChest = true;

        @Setting(displayName = "Show Emerald Count in Inventory", description = "Should your emerald count be displayed in your inventory?", order = 102)
        public boolean emeraldCountInventory = true;

        @Setting(displayName = "Show Emerald Count as Text", description = "Should your emerald count be displayed as text instead of icons?", order = 103)
        public boolean emeraldCountText = false;

        @Setting(displayName = "Highlight Crafting Ingredients", description = "Should crafting ingredients be highlighted according to their tier?", order = 40)
        public boolean ingredientHighlight = true;

        @Setting(displayName = "Highlight Duplicate Cosmetics", description = "Should duplicate cosmetics be highlighted on the scrap menu", order = 41)
        public boolean highlightCosmeticDuplicates = true;

        @Setting(displayName = "Minimum Powder Tier Highlight", description = "What should the minimum tier of powders be for it to be highlighted?\n\n§8Set to 0 to disable.", order = 42)
        @Setting.Limitations.IntLimit(min = 0, max = 6)
        public int minPowderTier = 4;

        @Setting(displayName = "Legendary Item Highlight Colour", description = "What colour should the highlight for legendary items be?\n\n§aClick the coloured box to open the colour wheel.", order = 50)
        public CustomColor lengendaryHighlightColor = new CustomColor(0f, 1f, 1f);

        @Setting(displayName = "Mythic Item Highlight Colour", description = "What colour should the highlight for mythic items be?\n\n§aClick the coloured box to open the colour wheel.", order = 51)
        public CustomColor mythicHighlightColor = new CustomColor(0.3f, 0, 0.3f);

        @Setting(displayName = "Fabled Item Highlight Colour", description = "What colour should the highlight for fabled items be?\n\n§aClick the coloured box to open the colour wheel.", order = 52)
        public CustomColor fabledHighlightColor = new CustomColor(1, 1/3f, 1/3f);

        @Setting(displayName = "Rare Item Highlight Colour", description = "What colour should the highlight for rare items be?\n\n§aClick the coloured box to open the colour wheel.", order = 53)
        public CustomColor rareHighlightColor = new CustomColor(1f, 0f, 1f);

        @Setting(displayName = "Unique Item Highlight Colour", description = "What colour should the highlight for unique items be?\n\n§aClick the coloured box to open the colour wheel.", order = 54)
        public CustomColor uniqueHighlightColor = new CustomColor(1f, 1f, 0f);

        @Setting(displayName = "Set Item Highlight Colour", description = "What colour should the highlight for set items be?\n\n§aClick the coloured box to open the colour wheel.", order = 55)
        public CustomColor setHighlightColor = new CustomColor(0f, 1f, 0f);

        @Setting(displayName = "Normal Item Highlight Colour", description = "What colour should the highlight for normal items be?\n\n§aClick the coloured box to open the colour wheel.", order = 56)
        public CustomColor normalHighlightColor = new CustomColor(1f, 1f, 1f);

        @Setting(displayName = "Crafted Armour & Weapon Highlight Colour", description = "What colour should the highlight for crafted armour and weapons be?\n\n§aClick the coloured box to open the colour wheel.", order = 57)
        public CustomColor craftedHighlightColor = new CustomColor(0, .545f, .545f);

        @Setting(displayName = "Ingredient Highlight Colour (1 star)", description = "What colour should the highlight for ingredients with one star be?\n\n§aClick the coloured box to open the colour wheel.", order = 58)
        public CustomColor ingredientOneHighlightColor = new CustomColor(1, 0.97f, 0.6f);

        @Setting(displayName = "Ingredient Highlight Colour (2 stars)", description = "What colour should the highlight for ingredients with two stars be?\n\n§aClick the coloured box to open the colour wheel.", order = 59)
        public CustomColor ingredientTwoHighlightColor = new CustomColor(1f, 1f, 0f);

        @Setting(displayName = "Ingredient Highlight Colour (3 stars)", description = "What colour should the highlight for ingredients with three stars be?\n\n§aClick the coloured box to open the colour wheel.", order = 60)
        public CustomColor ingredientThreeHighlightColor = new CustomColor(0.9f, .3f, 0);

        @Setting(displayName = "Inventory Item Highlight Opacity %", description = "How opaque should highlights in your inventory be? (As a percentage)", order = 61)
        @Setting.Limitations.FloatLimit(min = 0, max = 100, precision = 0.5f)
        public float inventoryAlpha = 100;

        @Setting(displayName = "Hotbar Item Highlight Opacity %", description = "Should the highlight of item rarities be displayed on the hotbar?\n\n§8Set to 0 to disable.", order = 62)
        @Setting.Limitations.FloatLimit(min = 0, max = 100, precision = 0.5f)
        public float hotbarAlpha = 30;


        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "market", displayPath = "Utilities/Market")
    public static class Market extends SettingsClass {
        public static Market INSTANCE;

        @Setting(displayName = "Display Market Prices in a Custom Format", description = "Should market prices be displayed in a custom format?")
        public boolean displayInCustomFormat = true;

        @Setting(displayName = "Market Prices Format", description = "What format should market prices be displayed in?\n\n§8Brackets indicate all parameters inside must not be 0.")
        @Setting.Features.StringParameters(parameters = { "les", "ebs", "es", "stx", "le", "eb", "e" })
        public String customFormat = "(%stx%stx )(%le%%les% )(%eb%%ebs% )(%e%%es%)";

        @Setting(displayName = "Open Chat", description = "Should the chat open when the trade market asks you to type a response?")
        public boolean openChatMarket = true;
    }

    @Override
    public void onSettingChanged(String name) {
        if (name.equalsIgnoreCase("addItemIdentificationSpacing"))
            WebManager.getDirectItems().forEach(ItemProfile::clearGuideStack);
        else if (name.equalsIgnoreCase("changeWindowTitle"))
            ServerEvents.onWindowTitleSettingChanged();
        else if (name.equalsIgnoreCase("changeWindowIcon")) {
            WindowIconManager.update();
        }
    }

}
