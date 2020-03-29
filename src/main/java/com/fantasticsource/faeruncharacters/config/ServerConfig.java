package com.fantasticsource.faeruncharacters.config;

import net.minecraftforge.common.config.Config;

import java.util.LinkedHashSet;

public class ServerConfig
{
    @Config.Ignore
    public LinkedHashSet<String>
            bareArmSkinSet = new LinkedHashSet<>(),
            markingsSet = new LinkedHashSet<>(),
            headAccessorySet = new LinkedHashSet<>(),
            faceAccessorySet = new LinkedHashSet<>();

    @Config.Name("Bare Arms")
    public String[] bareArms = new String[0];

    @Config.Name("Markings")
    public String[] markings = new String[0];

    @Config.Name("Head Accessories")
    public String[] headAccessories = new String[0];

    @Config.Name("Face Accessories")
    public String[] faceAccessories = new String[0];
}
