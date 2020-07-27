/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;


public class SpellEvent extends Event {
    public static class Cast extends SpellEvent {

        private final String spell;
        private final int manaCost;

        public Cast(String spell, int manaCost) {
            this.spell = spell;
            this.manaCost = manaCost;
        }

        public String getSpell() {
            return spell;
        }

        public int getManaCost() {
            return manaCost;
        }
    }

    public static class Totem extends SpellEvent {
    }

    public static class TotemSummoned extends Totem {
    }

    public static class TotemActivated extends Totem {
        private final int time;

        public TotemActivated(int time) {
            this.time = time;
        }

        public int getTime() {
            return time;
        }
    }

    public static class TotemRemoved extends Totem {
        private final boolean forcefullyRemoved;

        public TotemRemoved(boolean forcefullyRemoved) {
            this.forcefullyRemoved = forcefullyRemoved;
        }

        public boolean isForcefullyRemoved() {
            return forcefullyRemoved;
        }
    }

}
