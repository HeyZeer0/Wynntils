/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LabelBake;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.LocationProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class CommandDetection extends CommandBase implements IClientCommand {
    Map<String, List<LocationProfile>> mapFeatures = new HashMap<>();

    public CommandDetection() {
        for (LocationProfile location : WebManager.getNonIgnoredApiMarkers()) {
            mapFeatures.put(getFeatureKey(location), getProfileList(location));
        }
        for (LocationProfile location : WebManager.getNpcLocations()) {
            mapFeatures.put(getFeatureKey(location), Collections.singletonList(location));
        }
        for (LocationProfile location : WebManager.getMapLabels()) {
            mapFeatures.put(getFeatureKey(location), Collections.singletonList(location));
        }
    }

    private List<LocationProfile> getProfileList(LocationProfile location) {
        List<LocationProfile> knownProfiles = mapFeatures.get(getFeatureKey(location));
        if (knownProfiles == null) {
            knownProfiles = new LinkedList<>();
        }
        knownProfiles.add(location);
        return knownProfiles;
    }

    private String getFeatureKey(LocationProfile location) {
        return location.getTranslatedName().replace(" ", "_");
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "detection";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "detection <output filename>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException("/" + getUsage(sender));
        }

        String filename = args[0];

        try (PrintStream ps = new PrintStream(filename)) {

            int bakedCount = 0;
            for (LabelBake.BakerType type : LabelBake.BakerType.values()) {
                Map<Location, String> m = LabelBake.locationBaker.detectedTypes.get(type);
                for (Location key : m.keySet()) {
                    String name = m.get(key);
                    if (type == LabelBake.BakerType.BOOTH) {
                        name = "Booth Shop"; // hide current owner
                    }
                    printInstance(ps, type.name(), name, "...", key);
                    bakedCount++;
                }
            }

            int serviceCount = 0;
            for (Location key : LabelBake.detectedServices.keySet()) {
                String name = LabelBake.detectedServices.get(key);
                printInstance(ps, "Service", name, "...", key);
                serviceCount++;
            }

            int otherCount = 0;
            for (LabelBake.LabelLocation key : LabelBake.locationBaker.nameMap.keySet()) {
                String name = LabelBake.locationBaker.nameMap.get(key);
                String formattedName = LabelBake.locationBaker.formattedNameMap.get(key);
                Location location = LabelBake.locationBaker.otherLocMap.get(key);
                printInstance(ps, "Other", name, formattedName, location);
                otherCount++;
            }

            sender.sendMessage(new TextComponentString("Wrote " + bakedCount + " baked types, " + serviceCount + " services and " + otherCount + " other to " + filename));
        } catch (FileNotFoundException e) {
            sender.sendMessage(new TextComponentString("Invalid filename"));
            e.printStackTrace();
        }
    }

    private void printInstance(PrintStream ps, String type, String name, String formattedName, Location key) {
        // Write a CSV line
        ps.println(type + ", " + name + ", " + formattedName + ", " + (int) Math.floor(key.x) + ", " + (int) Math.floor(key.y) + ", " + (int) Math.floor(key.z));
    }

}
