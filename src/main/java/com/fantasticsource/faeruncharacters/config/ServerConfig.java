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

    @Config.Name("'None' Folder")
    public String noneFolder = "Era2/[None]/None";

    @Config.Name("Bare Arms")
    public String[] bareArms = new String[]{noneFolder};

    @Config.Name("Markings")
    public String[] markings = new String[]{noneFolder};

    @Config.Name("Head Accessories")
    public String[] headAccessories = new String[]{noneFolder};

    @Config.Name("Face Accessories")
    public String[] faceAccessories = new String[]{noneFolder};
}
