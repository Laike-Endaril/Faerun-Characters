package com.fantasticsource.faeruncharacters.config;

import net.minecraftforge.common.config.Config;

import java.util.HashSet;

public class ServerConfig
{
    @Config.Ignore
    public HashSet<String>
            bareArmSkinSet = new HashSet<>(),
            markingsSet = new HashSet<>(),
            headAccessorySet = new HashSet<>(),
            faceAccessorySet = new HashSet<>();

    @Config.Name("Bare Arms")
    public String[] bareArms = new String[0];

    @Config.Name("Markings")
    public String[] markings = new String[0];

    @Config.Name("Head Accessories")
    public String[] headAccessories = new String[0];

    @Config.Name("Face Accessories")
    public String[] faceAccessories = new String[0];
}
