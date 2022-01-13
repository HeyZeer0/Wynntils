package com.wynntils.modules.richpresence.discordgamesdk;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.wynntils.modules.richpresence.discordgamesdk.options.DiscordGameSDKOptions;

/**
 * <i>native declaration : line 316</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class DiscordImeUnderline extends Structure implements DiscordGameSDKOptions {

    public int from;
    public int to;
    public int color;
    public int background_color;
    public byte thick;

    public DiscordImeUnderline() {
        super();
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("from", "to", "color", "background_color", "thick");
    }

    public DiscordImeUnderline(int from, int to, int color, int background_color, byte thick) {
        super();
        this.from = from;
        this.to = to;
        this.color = color;
        this.background_color = background_color;
        this.thick = thick;
    }

    public DiscordImeUnderline(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends DiscordImeUnderline implements Structure.ByReference {

    };

    public static class ByValue extends DiscordImeUnderline implements Structure.ByValue {

    };
}
