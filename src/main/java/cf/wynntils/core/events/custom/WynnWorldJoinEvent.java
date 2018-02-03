package cf.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class WynnWorldJoinEvent extends Event {

    String world;

    public WynnWorldJoinEvent(String world) {
        this.world = world;
    }

    public String getWorld() {
        return world;
    }

}
