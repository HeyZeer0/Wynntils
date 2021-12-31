/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.ActionBarData;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.SpellData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.core.managers.PacketQueue;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.core.framework.instances.data.SpellData.SPELL_LEFT;
import static com.wynntils.core.framework.instances.data.SpellData.SPELL_RIGHT;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    private static final int[] spellUnlock = { 1, 11, 21, 31 };

    private static final Pattern WEAPON_SPEED_PATTERN = Pattern.compile("§7.+ Attack Speed");
    private static final Pattern CLASS_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Class Req:.+");
    private static final Pattern COMBAT_LVL_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Combat Lv. Min:.+");
    private static final Pattern SPELL_POINT_MIN_NOT_REACHED_PATTERN = Pattern.compile("§c✖§7 (.+) Min: (\\d+)");

    private static void queueSpell(int spellNumber, boolean a, boolean b, boolean c) {
        if (!canCastSpell(spellNumber)) return;

        int level = PlayerInfo.get(CharacterData.class).getLevel();
        boolean isLowLevel = level <= 11;
        Class<?> packetClass = isLowLevel ? SPacketTitle.class : SPacketChat.class;
        PacketQueue.queueComplexPacket(a == SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, 0, a, isLowLevel));
        PacketQueue.queueComplexPacket(b == SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, 1, b, isLowLevel));
        PacketQueue.queueComplexPacket(c == SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, 2, c, isLowLevel));
    }

    public static void castFirstSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(1, SPELL_LEFT, SPELL_RIGHT, SPELL_LEFT);
            return;
        }

        queueSpell(1, SPELL_RIGHT, SPELL_LEFT, SPELL_RIGHT);
    }

    public static void castSecondSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(2, SPELL_LEFT, SPELL_LEFT, SPELL_LEFT);
            return;
        }

        queueSpell(2, SPELL_RIGHT, SPELL_RIGHT, SPELL_RIGHT);
    }

    public static void castThirdSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(3, SPELL_LEFT, SPELL_RIGHT, SPELL_RIGHT);
            return;
        }

        queueSpell(3, SPELL_RIGHT, SPELL_LEFT, SPELL_LEFT);
    }

    public static void castFourthSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(4, SPELL_LEFT, SPELL_LEFT, SPELL_RIGHT);
            return;
        }

        queueSpell(4, SPELL_RIGHT, SPELL_RIGHT, SPELL_LEFT);
    }

    private static boolean canCastSpell(int spell) {
        if (!Reference.onWorld || !PlayerInfo.get(CharacterData.class).isLoaded()) {
            return false;
        }

        if (PlayerInfo.get(CharacterData.class).getLevel() < spellUnlock[spell - 1]) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You have not yet unlocked this spell! You need to be level " + spellUnlock[spell - 1]
            ));
            return false;
        }

        if (PlayerInfo.get(CharacterData.class).getCurrentMana() == 0) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You do not have enough mana to cast this spell!"
            ));
            return false;
        }

        ItemStack heldItem = McIf.player().getHeldItemMainhand();

        List<String> lore = ItemUtils.getLore(heldItem);

        //If item has attack speed line, it is a weapon
        boolean isWeapon = false;
        //Check class reqs to see if the weapon can be used by current class
        boolean classReqOk = false;
        //Is the current combat level enough to use the weapon
        boolean combatLvlMinReached = false;
        //If there is a spell point requirement that is not reached, store it and print it later
        String notReachedSpellPointRequirements = null;

        int i = 0;
        for (; i < lore.size(); i++) {
            if (WEAPON_SPEED_PATTERN.matcher(lore.get(i)).matches())
            {
                isWeapon = true;
                break;
            }
        }

        if (!isWeapon)
        {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The held item is not a weapon."
            ));
            return false;
        }

        for (; i < lore.size(); i++) {
            if (CLASS_REQ_OK_PATTERN.matcher(lore.get(i)).matches())
            {
                classReqOk = true;
                break;
            }
        }

        if (!classReqOk)
        {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The held weapon is not for this class."
            ));
            return false;
        }

        for (; i < lore.size(); i++) {
            if (COMBAT_LVL_REQ_OK_PATTERN.matcher(lore.get(i)).matches())
            {
                combatLvlMinReached = true;
                break;
            }
        }

        if (!combatLvlMinReached)
        {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The current class level is too low to use the held weapon."
            ));
            return false;
        }

        for (; i < lore.size(); i++) {
            Matcher matcher = SPELL_POINT_MIN_NOT_REACHED_PATTERN.matcher(lore.get(i));
            if (matcher.matches())
            {
                notReachedSpellPointRequirements = matcher.group(1);
                break;
            }
        }

        if (notReachedSpellPointRequirements != null) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The current class does not have enough "+notReachedSpellPointRequirements+" to use the held weapon."
            ));
            return false;
        }

        return true;
    }

    private static boolean checkKey(Packet<?> input, int pos, boolean clickType, boolean isLowLevel) {
        boolean[] spell;

        SpellData data = PlayerInfo.get(SpellData.class);
        if (isLowLevel) {
            SPacketTitle title = (SPacketTitle) input;
            if (title.getType() != SPacketTitle.Type.SUBTITLE) return false;

            spell = data.parseSpellFromTitle(McIf.getFormattedText(title.getMessage()));
        } else {
            SPacketChat title = (SPacketChat) input;
            if (title.getType() != ChatType.GAME_INFO) return false;

            PlayerInfo.get(ActionBarData.class).updateActionBar(McIf.getUnformattedText(title.getChatComponent()));

            spell = data.getLastSpell();
        }

        return pos < spell.length && spell[pos] == clickType;
    }

}
