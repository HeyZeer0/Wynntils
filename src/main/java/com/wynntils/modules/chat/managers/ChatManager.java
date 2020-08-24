/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.chat.managers;

import com.wynntils.ModCore;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.helpers.TextAction;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.utilities.configs.TranslationConfig;
import com.wynntils.webapi.services.TranslationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {

    public static DateFormat dateFormat;
    public static boolean validDateFormat;
    public static Pattern translationPatternChat = Pattern.compile("^(" +
            "(?:§8\\[[0-9]{1,3}/[A-Z][a-z](?:/[A-Za-z]+)?\\] §r§7\\[[^]]+\\] [^:]*: §r§7)" + "|" +  // local chat
            "(?:§3.* \\[[^]]+\\] shouts: §r§b)" + "|" + // shout
            "(?:§7\\[§r§e.*§r§7\\] §r§f)" + "|" + // party chat
            "(?:§7\\[§r.*§r§6 ➤ §r§2.*§r§7\\] §r§f)" + // private msg
            ")(.*)(§r)$");
    public static Pattern translationPatternNpc = Pattern.compile("^(" +
            "(?:§7\\[[0-9]+/[0-9]+\\] §r§2[^:]*: §r§a)" + // npc talking
            ")(.*)(§r)$");
    public static Pattern translationPatternOther = Pattern.compile("^(" +
            "(?:§5[^:]*: §r§d)" + "|" +  // interaction with e.g. blacksmith
            "(?:§[0-9a-z])" + // generic system message
            ")(.*)(§r)$");

    private static final SoundEvent popOffSound = new SoundEvent(new ResourceLocation("minecraft", "entity.blaze.hurt"));

    private static final String nonTranslatable = "[^a-zA-Z.!?]";
    private static final String optionalTranslatable = "[.!?]";

    private static final Pattern inviteReg = Pattern.compile("((" + TextFormatting.GOLD + "|" + TextFormatting.AQUA + ")/(party|guild) join [a-zA-Z0-9._-]+)");
    private static final Pattern tradeReg = Pattern.compile("\\w+ would like to trade! Type /trade \\w+ to accept\\.");
    private static final Pattern duelReg = Pattern.compile("\\w+ \\[Lv\\. \\d+] would like to duel! Type /duel \\w+ to accept\\.");
    private static final Pattern coordinateReg = Pattern.compile("(-?\\d{1,5}[ ,]{1,2})(\\d{1,3}[ ,]{1,2})?(-?\\d{1,5})");

    public static ITextComponent processRealMessage(ITextComponent in) {
        if (in == null) return in;
        ITextComponent original = in.createCopy();

        // Reorganizing
        if (!in.getUnformattedComponentText().isEmpty()) {
            ITextComponent newMessage = new TextComponentString("");
            newMessage.setStyle(in.getStyle().createDeepCopy());
            newMessage.appendSibling(in);
            newMessage.getSiblings().addAll(in.getSiblings());
            in.getSiblings().clear();
            in = newMessage;
        }

        // language translation
        if (TranslationConfig.INSTANCE.enableTextTranslation) {
            boolean wasTranslated = translateMessage(in);
            if (wasTranslated && !TranslationConfig.INSTANCE.keepOriginal) return null;
        }

        // timestamps
        if (ChatConfig.INSTANCE.addTimestampsToChat) {
            if (dateFormat == null || !validDateFormat) {
                try {
                    dateFormat = new SimpleDateFormat(ChatConfig.INSTANCE.timestampFormat);
                    validDateFormat = true;
                } catch (IllegalArgumentException ex) {
                    validDateFormat = false;
                }
            }

            List<ITextComponent> timeStamp = new ArrayList<>();
            ITextComponent startBracket = new TextComponentString("[");
            startBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
            timeStamp.add(startBracket);
            ITextComponent time;
            if (validDateFormat) {
                time = new TextComponentString(dateFormat.format(new Date()));
                time.getStyle().setColor(TextFormatting.GRAY);
            } else {
                time = new TextComponentString("Invalid Format");
                time.getStyle().setColor(TextFormatting.RED);
            }
            timeStamp.add(time);
            ITextComponent endBracket = new TextComponentString("] ");
            endBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
            timeStamp.add(endBracket);
            in.getSiblings().addAll(0, timeStamp);
        }

        // popup sound
        if (in.getUnformattedText().contains(" requires your ") && in.getUnformattedText().contains(" skill to be at least "))
            ModCore.mc().player.playSound(popOffSound, 1f, 1f);

        // wynnic translator
        if (StringUtils.hasWynnic(in.getUnformattedText())) {
            List<ITextComponent> newTextComponents = new ArrayList<>();
            boolean capital = false;
            boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find();
            boolean foundStart = false;
            boolean foundEndTimestamp = !ChatConfig.INSTANCE.addTimestampsToChat;
            boolean previousWynnic = false;
            boolean translateIntoHover = !ChatConfig.INSTANCE.translateIntoChat;
            ITextComponent currentTranslatedComponents = new TextComponentString("");
            List<ITextComponent> currentOldComponents = new ArrayList<>();
            if (foundEndTimestamp && !in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/") && !isGuildOrParty) {
                foundStart = true;
            }
            for (ITextComponent component : in.getSiblings()) {
                String toAdd = "";
                String currentNonTranslated = "";
                StringBuilder oldText = new StringBuilder();
                StringBuilder number = new StringBuilder();
                for (char character : component.getUnformattedText().toCharArray()) {
                    if (StringUtils.isWynnicNumber(character)) {
                        if (previousWynnic) {
                            toAdd += currentNonTranslated;
                            oldText.append(currentNonTranslated);
                        } else {
                            if (translateIntoHover) {
                                ITextComponent newComponent = new TextComponentString(oldText.toString());
                                newComponent.setStyle(component.getStyle().createDeepCopy());
                                newTextComponents.add(newComponent);
                                oldText = new StringBuilder();
                                toAdd = "";
                            }
                            previousWynnic = true;
                        }
                        currentNonTranslated = "";
                        number.append(character);
                        if (translateIntoHover) {
                            oldText.append(character);
                        }
                    } else {
                        if (!number.toString().isEmpty()) {
                            toAdd += StringUtils.translateNumberFromWynnic(number.toString());
                            if (!translateIntoHover) {
                                oldText.append(StringUtils.translateNumberFromWynnic(number.toString()));
                            }
                            number = new StringBuilder();
                        }

                        if (StringUtils.isWynnic(character)) {
                            if (previousWynnic) {
                                toAdd += currentNonTranslated;
                                oldText.append(currentNonTranslated);
                                currentNonTranslated = "";
                            } else {
                                if (translateIntoHover) {
                                    ITextComponent newComponent = new TextComponentString(oldText.toString());
                                    newComponent.setStyle(component.getStyle().createDeepCopy());
                                    newTextComponents.add(newComponent);
                                    oldText = new StringBuilder();
                                    toAdd = "";
                                }
                                previousWynnic = true;
                            }
                            String englishVersion = StringUtils.translateCharacterFromWynnic(character);
                            if (capital && englishVersion.matches("[a-z]")) {
                                englishVersion = Character.toString(Character.toUpperCase(englishVersion.charAt(0)));
                            }

                            if (".?!".contains(englishVersion)) {
                                capital = true;
                            } else {
                                capital = false;
                            }
                            toAdd += englishVersion;
                            if (translateIntoHover) {
                                oldText.append(character);
                            } else {
                                oldText.append(englishVersion);
                            }
                        } else if (Character.toString(character).matches(nonTranslatable) || Character.toString(character).matches(optionalTranslatable)) {
                            if (previousWynnic) {
                                currentNonTranslated += character;
                            } else {
                                oldText.append(character);
                            }

                            if (".?!".contains(Character.toString(character))) {
                                capital = true;
                            } else if (character != ' ') {
                                capital = false;
                            }
                        } else {
                            if (previousWynnic) {
                                previousWynnic = false;
                                if (translateIntoHover) {
                                    ITextComponent oldComponent = new TextComponentString(oldText.toString());
                                    oldComponent.setStyle(component.getStyle().createDeepCopy());
                                    ITextComponent newComponent = new TextComponentString(toAdd);
                                    newComponent.setStyle(component.getStyle().createDeepCopy());

                                    newTextComponents.add(oldComponent);
                                    currentTranslatedComponents.appendSibling(newComponent);
                                    currentOldComponents.add(oldComponent);
                                    for (ITextComponent currentOldComponent : currentOldComponents) {
                                        currentOldComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, currentTranslatedComponents));
                                    }

                                    currentOldComponents.clear();
                                    currentTranslatedComponents = new TextComponentString("");

                                    oldText = new StringBuilder(currentNonTranslated);
                                } else {
                                    oldText.append(currentNonTranslated);
                                }
                                currentNonTranslated = "";
                            }
                            oldText.append(character);

                            if (character != ' ') {
                                capital = false;
                            }
                        }
                    }
                }
                if (!number.toString().isEmpty() && previousWynnic) {
                    toAdd += StringUtils.translateNumberFromWynnic(number.toString());
                }
                if (!currentNonTranslated.isEmpty()) {
                    oldText.append(currentNonTranslated);
                    if (previousWynnic) {
                        toAdd += currentNonTranslated;
                    }
                }

                ITextComponent oldComponent = new TextComponentString(oldText.toString());
                oldComponent.setStyle(component.getStyle().createDeepCopy());
                newTextComponents.add(oldComponent);
                if (previousWynnic && translateIntoHover) {
                    ITextComponent newComponent = new TextComponentString(toAdd);
                    newComponent.setStyle(component.getStyle().createDeepCopy());

                    currentTranslatedComponents.appendSibling(newComponent);
                    currentOldComponents.add(oldComponent);
                }
                if (!foundStart) {
                    if (foundEndTimestamp) {
                        if (in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/")) {
                            foundStart = component.getUnformattedText().contains(":");
                        } else if (isGuildOrParty) {
                            foundStart = component.getUnformattedText().contains("]");
                        }
                    } else if (component.getUnformattedComponentText().contains("] ")) {
                        foundEndTimestamp = true;
                        if (!in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/") && !isGuildOrParty) {
                            foundStart = true;
                        }
                    }

                    if (foundStart) {
                        capital = true;
                    }
                }
            }

            if (translateIntoHover) {
                for (ITextComponent component : currentOldComponents) {
                    component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, currentTranslatedComponents));
                }
            }

            in.getSiblings().clear();
            in.getSiblings().addAll(newTextComponents);
        }

        // clickable party invites
        if (ChatConfig.INSTANCE.clickablePartyInvites && inviteReg.matcher(in.getFormattedText()).find()) {
            for (ITextComponent textComponent : in.getSiblings()) {
                if (textComponent.getUnformattedComponentText().startsWith("/")) {
                    String command = textComponent.getUnformattedComponentText();
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Join!")));
                }
            }
        }

        // clickable trade messages
        if (ChatConfig.INSTANCE.clickableTradeMessage && tradeReg.matcher(in.getUnformattedText()).find()) {
            for (ITextComponent textComponent : in.getSiblings()) {
                if (textComponent.getUnformattedComponentText().startsWith("/")) {
                    String command = textComponent.getUnformattedComponentText();
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Trade!")));
                }
            }
        }

        // clickable duel messages
        if (ChatConfig.INSTANCE.clickableDuelMessage && duelReg.matcher(in.getUnformattedText()).find()) {
            for (ITextComponent textComponent : in.getSiblings()) {
                if (textComponent.getUnformattedComponentText().startsWith("/")) {
                    String command = textComponent.getUnformattedComponentText();
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Duel!")));
                }
            }
        }

        // clickable coordinates
        if (ChatConfig.INSTANCE.clickableCoordinates && coordinateReg.matcher(in.getUnformattedText()).find()) {
            String crdText;
            Style style;
            String command = "/compass ";
            List<ITextComponent> crdMsg = new ArrayList<>();

            for (ITextComponent texts: in.getSiblings()) {
                Matcher m = coordinateReg.matcher(texts.getUnformattedText());
                if (!m.find())  continue;

                // Most likely only needed during the Wynnter Fair for the message with how many more players are required to join.
                // As far as i could find all other messages from the Wynnter Fair use text components properly.
                if (m.start() > 0 && texts.getUnformattedText().charAt(m.start() - 1) == '§') continue;

                int index = in.getSiblings().indexOf(texts);

                crdText = texts.getUnformattedText();
                style = texts.getStyle();
                in.getSiblings().remove(texts);

                // Pre-text
                ITextComponent preText = new TextComponentString(crdText.substring(0, m.start()));
                preText.setStyle(style.createShallowCopy());
                crdMsg.add(preText);

                // Coordinates:
                command += crdText.substring(m.start(1), m.end(1)).replaceAll("[ ,]", "") + " ";
                command += crdText.substring(m.start(3), m.end(3)).replaceAll("[ ,]", "");
                ITextComponent clickableText = new TextComponentString(crdText.substring(m.start(), m.end()));
                clickableText.setStyle(style.createShallowCopy());
                clickableText.getStyle()
                        .setColor(TextFormatting.DARK_AQUA)
                        .setUnderlined(true)
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
                crdMsg.add(clickableText);

                // Post-text
                ITextComponent postText = new TextComponentString(crdText.substring(m.end()));
                postText.setStyle(style.createShallowCopy());
                crdMsg.add(postText);

                in.getSiblings().addAll(index, crdMsg);
                break;
            }
        }
        
        //powder manual
        if (ChatConfig.INSTANCE.customPowderManual && in.getUnformattedText().equals("                         Powder Manual")) {
            List<ITextComponent> chapterSelect = new ArrayList<ITextComponent>();
            
            ITextComponent offset = new TextComponentString("\n               "); //to center chapter select
            ITextComponent spacer = new TextComponentString("   "); //space between chapters
            
            chapterSelect.add(offset);
            
            for (int i = 1; i <= 3; i++) {
                ITextComponent chapter = new TextComponentString("Chapter " + i);
                chapter.getStyle()
                        .setColor(TextFormatting.GOLD)
                        .setUnderlined(true)
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to read Chapter " + i)));
                chapter = TextAction.withDynamicEvent(chapter, new ChapterReader(i));
                
                chapterSelect.add(chapter);
                chapterSelect.add(spacer);
            }
            
            chapterSelect.add(new TextComponentString("\n"));
            in.getSiblings().addAll(chapterSelect);
            
        }

        return in;
    }

    private static boolean translateMessage(ITextComponent in) {
        if (!in.getUnformattedText().startsWith(TranslationManager.TRANSLATED_PREFIX)) {
            String formatted = in.getFormattedText();
            Matcher chatMatcher = translationPatternChat.matcher(formatted);
            if (chatMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translatePlayerChat) return false;
                sendTranslation(chatMatcher);
                return true;
            }
            Matcher npcMatcher = translationPatternNpc.matcher(formatted);
            if (npcMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translateNpc) return false;
                sendTranslation(npcMatcher);
                return true;
            }
            Matcher otherMatcher = translationPatternOther.matcher(formatted);
            if (otherMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translateOther) return false;
                sendTranslation(otherMatcher);
                return true;
            }
        }

        return false;
    }

    private static void sendTranslation(Matcher m) {
        // We only want to translate the actual message, not formatting, sender, etc.
        String message = TextFormatting.getTextWithoutFormattingCodes(m.group(2));
        String prefix = m.group(1);
        String suffix = m.group(3);
        TranslationManager.getTranslator().translate(message, TranslationConfig.INSTANCE.languageName, translatedMsg -> {
            try {
                // Don't want translation to appear before original
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
            Minecraft.getMinecraft().addScheduledTask(() ->
                    ChatOverlay.getChat().printChatMessage(new TextComponentString(TranslationManager.TRANSLATED_PREFIX + prefix + translatedMsg + suffix)));
        });
    }

    public static ITextComponent renderMessage(ITextComponent in) {
        return in;
    }

    public static boolean processUserMention(ITextComponent in, ITextComponent original) {
        if (ChatConfig.INSTANCE.allowChatMentions && in != null && Minecraft.getMinecraft().player != null) {
            String match = ModCore.mc().player.getName() + (ChatConfig.INSTANCE.mentionNames.length() > 0 ? "|" + ChatConfig.INSTANCE.mentionNames.replace(",", "|") : "");
            Pattern pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);

            Matcher looseMatcher = pattern.matcher(in.getUnformattedText());

            if (looseMatcher.find()) {
                boolean hasMention = false;

                boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find();
                boolean foundStart = false;
                boolean foundEndTimestamp = !ChatConfig.INSTANCE.addTimestampsToChat;

                List<ITextComponent> components = new ArrayList<>();

                for (ITextComponent component : in.getSiblings()) {
                    String text = component.getUnformattedText();

                    if (!foundEndTimestamp) {
                        foundEndTimestamp = text.contains("]");
                        components.add(component);
                        continue;
                    }

                    if (!foundStart) {
                        foundStart = text.contains((isGuildOrParty ? "]" : ":")); // Party and guild messages end in ']' while normal chat end in ':'
                        components.add(component);
                        continue;
                    }

                    Matcher matcher = pattern.matcher(text);

                    int nextStart = 0;

                    while (matcher.find()) {
                        hasMention = true;

                        String before = text.substring(nextStart, matcher.start());
                        String name = text.substring(matcher.start(), matcher.end());

                        nextStart = matcher.end();

                        ITextComponent beforeComponent = new TextComponentString(before);
                        beforeComponent.setStyle(component.getStyle().createShallowCopy());

                        ITextComponent nameComponent = new TextComponentString(name);
                        nameComponent.setStyle(component.getStyle().createShallowCopy());
                        nameComponent.getStyle().setColor(TextFormatting.YELLOW);

                        components.add(beforeComponent);
                        components.add(nameComponent);
                    }

                    ITextComponent afterComponent = new TextComponentString(text.substring(nextStart));
                    afterComponent.setStyle(component.getStyle().createShallowCopy());

                    components.add(afterComponent);
                }
                if (hasMention) {
                    ModCore.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));
                    in.getSiblings().clear();
                    in.getSiblings().addAll(components);

                    return true; // Marks the chat tab as containing a mention
                }
            }
        }
        return false;
    }

    public static Pair<String, Boolean> applyUpdatesToServer(String message) {
        String after = message;

        boolean cancel = false;

        if (message.contains("{")) {
            StringBuilder newString = new StringBuilder();
            boolean isWynnic = false;
            boolean isNumber = false;
            boolean invalidNumber = false;
            int number = 0;
            StringBuilder oldNumber = new StringBuilder();
            for (char character : message.toCharArray()) {
                if (character == '{') {
                    isWynnic = true;
                    isNumber = false;
                    number = 0;
                    oldNumber = new StringBuilder();
                } else if (isWynnic && character == '}') {
                    isWynnic = false;
                } else if (isWynnic) {
                    if (Character.isDigit(character) && !invalidNumber) {
                        if (oldNumber.toString().endsWith(".")) {
                            invalidNumber = true;
                            isNumber = false;
                            newString.append(oldNumber);
                            newString.append(character);
                            oldNumber = new StringBuilder();
                            number = 0;
                            continue;
                        }
                        number = number * 10 + Integer.parseInt(Character.toString(character));
                        oldNumber.append(character);
                        if (number >= 400) {
                            invalidNumber = true;
                            isNumber = false;
                            newString.append(oldNumber);
                            oldNumber = new StringBuilder();
                            number = 0;
                        } else {
                            isNumber = true;
                        }
                    } else if (character == ',' && isNumber) {
                        oldNumber.append(character);
                    } else if (character == '.' && isNumber) {
                        oldNumber.append('.');
                    } else {
                        if (isNumber) {
                            if (1 <= number && number <= 9) {
                                newString.append((char) (number + 0x2473));
                            } else if (number == 10 || number == 50 || number == 100) {
                                switch (number) {
                                    case 10:
                                        newString.append('⑽');
                                        break;
                                    case 50:
                                        newString.append('⑾');
                                        break;
                                    case 100:
                                        newString.append('⑿');
                                        break;
                                }
                            } else if (1 <= number && number <= 399) {
                                int hundreds = number / 100;
                                for (int hundred = 1; hundred <= hundreds; hundred++) {
                                    newString.append('⑿');
                                }

                                int tens = (number % 100) / 10;
                                if (1 <= tens && tens <= 3) {
                                    for (int ten = 1; ten <= tens; ten++) {
                                        newString.append('⑽');
                                    }
                                } else if (4 == tens) {
                                    newString.append("⑽⑾");
                                } else if (5 <= tens && tens <= 8) {
                                    newString.append('⑾');
                                    for (int ten = 1; ten <= tens - 5; ten++) {
                                        newString.append('⑽');
                                    }
                                } else if (9 == tens) {
                                    newString.append("⑽⑿");
                                }

                                int ones = number % 10;
                                if (1 <= ones) {
                                    newString.append((char) (ones + 0x2473));
                                }
                            } else {
                                newString.append(number);
                            }
                            number = 0;
                            isNumber = false;
                            if (oldNumber.toString().endsWith(",")) {
                                newString.append(',');
                            }
                            if (oldNumber.toString().endsWith(".")) {
                                newString.append('０');
                            }
                            oldNumber = new StringBuilder();
                        }

                        if (invalidNumber && !Character.isDigit(character)) {
                            invalidNumber = false;
                        }

                        if (!Character.toString(character).matches(nonTranslatable)) {
                            if (Character.toString(character).matches("[a-z]")) {
                                newString.append((char) ((character) + 0x243B));
                            } else if (Character.toString(character).matches("[A-Z]")) {
                                newString.append((char) ((character) + 0x245B));
                            } else if (character == '.') {
                                newString.append('０');
                            } else if (character == '!') {
                                newString.append('１');
                            } else if (character == '?') {
                                newString.append('２');
                            }
                        } else {
                            newString.append(character);
                        }
                    }
                } else {
                    newString.append(character);
                }
            }

            if (isNumber) {
                if (1 <= number && number <= 9) {
                    newString.append((char) (number + 0x2473));
                } else if (number == 10 || number == 50 || number == 100) {
                    switch (number) {
                        case 10:
                            newString.append('⑽');
                            break;
                        case 50:
                            newString.append('⑾');
                            break;
                        case 100:
                            newString.append('⑿');
                            break;
                    }
                } else if (1 <= number && number <= 399) {
                    int hundreds = number / 100;
                    for (int hundred = 1; hundred <= hundreds; hundred++) {
                        newString.append('⑿');
                    }

                    int tens = (number % 100) / 10;
                    if (1 <= tens && tens <= 3) {
                        for (int ten = 1; ten <= tens; ten++) {
                            newString.append('⑽');
                        }
                    } else if (4 == tens) {
                        newString.append("⑽⑾");
                    } else if (5 <= tens && tens <= 8) {
                        newString.append('⑾');
                        for (int ten = 1; ten <= tens - 5; ten++) {
                            newString.append('⑽');
                        }
                    } else if (9 == tens) {
                        newString.append("⑽⑿");
                    }

                    int ones = number % 10;
                    if (1 <= ones) {
                        newString.append((char) (ones + 0x2473));
                    }
                } else {
                    newString.append(number);
                }

                if (oldNumber.toString().endsWith(",")) {
                    newString.append(',');
                }
                if (oldNumber.toString().endsWith(".")) {
                    newString.append('０');
                }
            }

            after = newString.toString();

        }

        return new Pair<>(after, cancel);
    }
    
    private static class ChapterReader implements Runnable {
        
        private static final String CHAPTER_1 = "                     §6Chapter 1 - The Basics\n\n" +
                "    §7§oThere exist five varieties of magic powder, each corresponding to one of five different elements; §c✹ Fire§7§o, §b❉ Water§7§o, §e✦ Thunder§7§o, §2✤ Earth§7§o, and §f❋ Air§7§o.\n" +
                "    §7§oIt is possible to augment items with these powders, if one is versed in the art of enchantment. Many offer this service for a price, as Powder Masters.\n" +
                "    Certain items have a higher magical capacity, and thusly are able to hold more powders within them. Some have many, some have few, others still have none.\n" +
                "    Should a §lweapon§r§7§o be enchanted with a powder, the powder will imbue the item with a slight amount of elemental damage, but also transfer basic neutral damage into its element.\n" +
                "    Should a piece of §larmour§r§7§o be enchanted with a powder, the garment will gain a resistance towards that powder's element, at the cost of an elemental weakness.";
        
        private static final String CHAPTER_2 = "                  §6Chapter 2 - Elemental Abilities\n\n" +
                "    §7§oIf a powder is more concentrated, they can unlock powerful elemental magicks within an item. Powders appraised to be Tier IV or higher are capable of forming these special abilities.\n" +
                "    Two or more like-elemented powders are necessary for this. While items can be augmented with powders even after unlocking an ability, only one amplified magic power can be kept within an item at one time.\n" +
                "    Weapons and armour channel this magical energy in radically different ways, just as powders change their more mundane abilities. The method of use for each piece can be referred to in Chapter 3.\n" +
                "    Using different tiers of powder to try to form an ability on an item is somewhat unpredictable in the end potency of the magic, but using higher tiers of powder will generally lead to a stronger effect.\n" +
                "    To unleash the unique ability imbued in your weapon, one must first charge the power by attacking enemies. Then, heavily focus on the latent energies within the item, and release them with a swing. §r(Shift+Left-Click)";
        
        private static final String CHAPTER_3 = "                  §6Chapter 3 - Elemental Abilities\n\n" +
                "    §2§l✤ Earth:  §7§oEarth-imbued weapons will unleash a powerful quake, rupturing the nearby ground and disorienting enemies. Earth-augmented armors produce a rage-state in the user as they near death, turning Earth-based attacks more potent, and more violent.\n" +
                "    §e§l✦ Thunder:  §7§oThunder-imbued weapons can release a streak of lightning that seeks enemies, bouncing from one foe to the next nearby. Thunder-augmented armors will steal the life force of the slain, and in kind increase the power of Thunder-based attacks temporarily.\n" +
                "    §b§l❉ Water:  §7§oWater-imbued weapons will curse one's nearby enemies. This curse weakens armor, and turns them vulnerable to further attack. Water-augmented armors recycle used mana, transferring it into a short boost to Water-affiliated techniques, depending on how much mana was consumed.\n" +
                "    §c§l✹ Fire:  §7§oFire-imbued weapons will generate a fan of flames that burn one's enemies and empower one's allies. Fire-augmented armors react to being struck, turning the impact of the blow, no matter how weak, into a temporary power boost to Fire-based attacks.\n" +
                "    §f§l❋ Air:  §7§oAir-imbued weapons trap nearby enemies in a vortex of wind, and blow the victims away when struck. Air-augmented armors will, so long as they are kept close, leach energy from nearby foes to improve the strength of Air-based attacks.";
        
        ITextComponent chapterText;

        public ChapterReader(int chapter) {
            String text;
            switch (chapter) {
                case 1:
                    text = CHAPTER_1;
                    break;
                case 2:
                    text = CHAPTER_2;
                    break;
                case 3:
                    text = CHAPTER_3;
                    break;
                default: text = "";
            }
            chapterText = new TextComponentString(text);
            chapterText.getStyle()
                    .setColor(TextFormatting.GRAY)
                    .setItalic(true);
        }
        
        @Override
        public void run() {
            Minecraft.getMinecraft().player.sendMessage(chapterText);
        }
        
    }

}
