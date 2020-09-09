/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.ui.elements.GuiTextFieldWynn;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.SkillPointAllocation;
import com.wynntils.modules.utilities.overlays.ui.BuildsUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillPointOverlay implements Listener {
    
    private static final Pattern SKILLPOINT_PATTERN = Pattern.compile(".*?([-0-9]+)(?=\\spoints).*");
    private static final Pattern[] MODIFIER_PATTERNS = {
            Pattern.compile("- Strength: ([-+0-9]+)"),
            Pattern.compile("- Dexterity: ([-+0-9]+)"),
            Pattern.compile("- Intelligence: ([-+0-9]+)"),
            Pattern.compile("- Defence: ([-+0-9]+)"),
            Pattern.compile("- Agility: ([-+0-9]+)"),
    };
    
    private static final int SAVE_SLOT = 1;
    private static final int LOAD_SLOT = 3;
    
    private GuiTextFieldWynn nameField;
    
    private int skillPointsRemaining;
    private SkillPointAllocation loadedBuild = null;
    private boolean startBuild = false;
    private boolean itemsLoaded = false;
    
    @SubscribeEvent
    public void onChestClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        nameField = null;
        Keyboard.enableRepeatEvents(false);
        itemsLoaded = false;
    }

    @SubscribeEvent
    public void onDrawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        addManaTables(e.getGui());
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {
        Matcher m = Utils.CHAR_INFO_PAGE_TITLE.matcher(e.getGui().getLowerInv().getName());
        if (!m.find()) return;

        skillPointsRemaining = Integer.parseInt(m.group(1));
        
        // load/save icons
        ItemStack save = new ItemStack(Items.WRITABLE_BOOK);
        save.setStackDisplayName(TextFormatting.GOLD + "[>] Save current build");
        ItemUtils.replaceLore(save, Arrays.asList(TextFormatting.GRAY + "Allows you to save this build with a name"));
        e.getGui().inventorySlots.getSlot(SAVE_SLOT).putStack(save);
        
        ItemStack load = new ItemStack(Items.ENCHANTED_BOOK);
        load.setStackDisplayName(TextFormatting.GOLD + "[>] Load build");
        ItemUtils.replaceLore(load, Arrays.asList(TextFormatting.GRAY + "Allows you to load one of your saved builds"));
        e.getGui().inventorySlots.getSlot(LOAD_SLOT).putStack(load);
        
        // skill point allocating
        if (!itemsLoaded || startBuild) {
            startBuild = false;
            allocateSkillPoints(e.getGui());
        }

        for (int i = 0; i < e.getGui().getLowerInv().getSizeInventory(); i++) {
            ItemStack stack = e.getGui().getLowerInv().getStackInSlot(i);
            if (stack.isEmpty() || !stack.hasDisplayName()) continue; // display name also checks for tag compound

            String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack));
            String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            int value = 0;

            if (name.contains("Upgrade")) {// Skill Points
                Matcher spm = SKILLPOINT_PATTERN.matcher(lore);
                if (!spm.find()) continue;
                
                value = Integer.parseInt(spm.group(1));
            } else if (name.contains("Profession")) { // Profession Icons
                int start = lore.indexOf("Level: ") + 7;
                int end = lore.indexOf("XP: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("'s Info")) { // Combat level on Info
                int start = lore.indexOf("Combat Lv: ") + 11;
                int end = lore.indexOf("Class: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("Damage Info")) { //Average Damage
                Pattern pattern = Pattern.compile("Total Damage \\(\\+Bonus\\): ([0-9]+)-([0-9]+)");
                Matcher m2  = pattern.matcher(lore);
                if (!m2.find()) continue;

                int min = Integer.parseInt(m2.group(1));
                int max = Integer.parseInt(m2.group(2));

                value = Math.round((max + min) / 2.0f);
            } else if (name.contains("Daily Rewards")) { //Daily Reward Multiplier
                int start = lore.indexOf("Streak Multiplier: ") + 19;
                int end = lore.indexOf("Log in everyday to");

                value = Integer.parseInt(lore.substring(start, end));
            } else continue;

            stack.setCount(value <= 0 ? 1 : value);
        }
    }
    
    @SubscribeEvent
    public void onChestForeground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;
        
        // draw name field
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 500);
        if (nameField != null) nameField.drawTextBox();
        GlStateManager.popMatrix();
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChestGui(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            String name = TextFormatting.getTextWithoutFormattingCodes(s.getStack().getDisplayName());
            SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
            if (skillPoint != null) {
                ScreenRenderer.beginGL(e.getGui().getGuiLeft() , e.getGui().getGuiTop());
                GlStateManager.translate(0, 0, 251);
                ScreenRenderer r = new ScreenRenderer();
                RenderHelper.disableStandardItemLighting();
                r.drawString(skillPoint.getColoredSymbol(), s.xPos + 2, s.yPos, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                ScreenRenderer.endGL();
            }
        }
    }
    
    @SubscribeEvent
    public void onSlotClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;
        
        if (e.getSlotId() == SAVE_SLOT) {
            e.setCanceled(true);
            nameField = new GuiTextFieldWynn(200, Minecraft.getMinecraft().fontRenderer, 8, 5, 130, 10);
            nameField.setFocused(true);
            nameField.setText("Enter build name");
            Keyboard.enableRepeatEvents(true);
        } else if (e.getSlotId() == LOAD_SLOT) {
            e.setCanceled(true);
            ModCore.mc().displayGuiScreen(new BuildsUI(this, ModCore.mc().currentScreen, new InventoryBasic("Builds", false, 54)));
        }
    }
    
    @SubscribeEvent
    public void onMouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        int offsetMouseX = e.getMouseX() - e.getGui().getGuiLeft();
        int offsetMouseY = e.getMouseY() - e.getGui().getGuiTop();

        // handle mouse input on name editor
        if (nameField != null) {
            nameField.mouseClicked(offsetMouseX, offsetMouseY, e.getMouseButton());
            if (e.getMouseButton() == 0) { // left click
                if (nameField.isFocused()) {
                    nameField.setCursorPositionEnd();
                    nameField.setSelectionPos(0);
                } else {
                    nameField.setSelectionPos(nameField.getCursorPosition());
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        // handle typing in text boxes
        if (nameField != null && nameField.isFocused()) {
            e.setCanceled(true);
            if (e.getKeyCode() == Keyboard.KEY_RETURN) {
                String name = nameField.getText();
                nameField = null;

                name = name.replaceAll("&([a-f0-9k-or])", "§$1");
                UtilitiesConfig.INSTANCE.savedBuilds.add(new Pair(name, getSkillPoints(e.getGui())));
                UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
                
            } else if (e.getKeyCode() == Keyboard.KEY_ESCAPE) {
                nameField = null;
            } else {
                nameField.textboxKeyTyped(e.getTypedChar(), e.getKeyCode());
            }
        } else if (e.getKeyCode() == Keyboard.KEY_ESCAPE || e.getKeyCode() == ModCore.mc().gameSettings.keyBindInventory.getKeyCode()) {
            loadedBuild = null; // inventory was closed by player
        }
    }

    private String remainingLevelsDescription(int remainingLevels) {
        return "" + TextFormatting.GOLD + remainingLevels + TextFormatting.GRAY + " point" + (remainingLevels == 1 ? "" : "s");
    }

    private int getIntelligencePoints(ItemStack stack) {
        String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack));
        int start = lore.indexOf(" points ") - 3;

        return Integer.parseInt(lore.substring(start, start + 3).trim());
    }

    public void addManaTables(ChestReplacer gui) {
        ItemStack stack = gui.getLowerInv().getStackInSlot(11);
        if (stack.isEmpty() || !stack.hasDisplayName()) return; // display name also checks for tag compound

        int intelligencePoints = getIntelligencePoints(stack);
        if (stack.getTagCompound().hasKey("wynntilsAnalyzed")) return;

        int closestUpgradeLevel = Integer.MAX_VALUE;
        int level = PlayerInfo.getPlayerInfo().getLevel();

        List<String> newLore = new LinkedList<>();

        for (int j = 0; j < 4; j++) {
            SpellType spell = SpellType.forClass(PlayerInfo.getPlayerInfo().getCurrentClass(), j + 1);

            if (spell.getUnlockLevel(1) <= level) {
                int nextUpgrade = spell.getNextManaReduction(level, intelligencePoints);
                if (nextUpgrade < closestUpgradeLevel) {
                    closestUpgradeLevel = nextUpgrade;
                }
                int manaCost = spell.getManaCost(level, intelligencePoints);
                String spellName = PlayerInfo.getPlayerInfo().isCurrentClassReskinned() ? spell.getReskinned() : spell.getName();
                String spellInfo = TextFormatting.LIGHT_PURPLE + spellName + " Spell: " + TextFormatting.AQUA
                        + "-" + manaCost + " ✺";
                if (nextUpgrade < Integer.MAX_VALUE) {
                    spellInfo += TextFormatting.GRAY + " (-" + (manaCost - 1) + " ✺ in "
                            + remainingLevelsDescription(nextUpgrade - intelligencePoints) + ")";
                }
                newLore.add(spellInfo);
            }
        }

        List<String> loreTag = new LinkedList<>(ItemUtils.getLore(stack));
        if (closestUpgradeLevel < Integer.MAX_VALUE) {
            loreTag.add("");
            loreTag.add(TextFormatting.GRAY + "Next upgrade: At " + TextFormatting.WHITE + closestUpgradeLevel
                    + TextFormatting.GRAY + " points (in " + remainingLevelsDescription(closestUpgradeLevel - intelligencePoints) + ")");
        }

        loreTag.add("");
        loreTag.addAll(newLore);

        ItemUtils.replaceLore(stack, loreTag);
        stack.getTagCompound().setBoolean("wynntilsAnalyzed", true);
    }
    
    private SkillPointAllocation getSkillPoints(ChestReplacer gui) {
        int[] sp = new int[5];
        
        for (int i = 0; i < 5; i++) {
            ItemStack stack = gui.getLowerInv().getStackInSlot(i + 9); // sp indicators start at 9
            Matcher m = SKILLPOINT_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack)));
            if (!m.find()) continue;
            
            sp[i] = Integer.parseInt(m.group(1));
        }
        
        // following code subtracts gear sp from total sp to find player-allocated sp
        ItemStack info = gui.getLowerInv().getStackInSlot(6); // player info slot
        for (String line : ItemUtils.getLore(info)) {
            String unformattedLine = TextFormatting.getTextWithoutFormattingCodes(line);
            for (int i = 0; i < 5; i++) {
                Matcher m = MODIFIER_PATTERNS[i].matcher(unformattedLine);
                if (!m.find()) continue;
                
                int modifier = Integer.parseInt(m.group(1));
                sp[i] -= modifier;
            }
        }
        
        return new SkillPointAllocation(sp[0], sp[1], sp[2], sp[3], sp[4]);
    }
    
    public void loadBuild(SkillPointAllocation build) {
        if (build.getTotalSkillPoints() > skillPointsRemaining) {
            TextComponentString text = new TextComponentString("Not enough free skill points!");
            text.getStyle().setColor(TextFormatting.RED);

            ModCore.mc().player.sendMessage(text);
            ModCore.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
            return;
        }
        
        loadedBuild = build;
        startBuild = true;
    }
    
    private void allocateSkillPoints(ChestReplacer gui) {
        if (loadedBuild == null) return;
        if (gui.inventorySlots.getSlot(9).getStack().isEmpty()) return;
        itemsLoaded = true;
        
        int[] currentSp = getSkillPoints(gui).getAsArray();
        int[] buildSp = loadedBuild.getAsArray();
        for (int i = 0; i < 5; i++) {
            if (currentSp[i] < buildSp[i]) {
                int button = (buildSp[i] - currentSp[i] >= 5) ? 1 : 0; // right click if difference >= 5 for efficiency
                CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, 9 + i, button, ClickType.PICKUP, gui.inventorySlots.getSlot(9 + i).getStack(),
                        gui.inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
                ModCore.mc().getConnection().sendPacket(packet);
                return; // can only click once at a time
            }
        }
        loadedBuild = null; // we've fully loaded the build if we reach this point
    }

}
