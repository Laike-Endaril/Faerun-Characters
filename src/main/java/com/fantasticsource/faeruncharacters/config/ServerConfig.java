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
    public String[] bareArms = new String[]{};

    @Config.Name("Markings")
    public String[] markings = new String[]{};

    @Config.Name("Head Accessories")
    public String[] headAccessories = new String[]{};

    @Config.Name("Face Accessories")
    public String[] faceAccessories = new String[]{};

    @Config.Name("Patreon Creator Access Token")
    public String patreonCreatorAccessToken = "";

    @Config.Name("Additional Premium Players")
    public String[] additionalPremiumPlayers = new String[0];

    @Config.Name("Cents For Premium Access")
    public int centsForPremium = 500;
}
