package com.fantasticsource.faeruncharacters.config;

import net.minecraftforge.common.config.Config;

public class ClientConfig
{
    @Config.Name("Button Color (Active)")
    public static String activeButtonColor = "00FFFF";

    @Config.Name("Button Color (Hover)")
    public static String hoverButtonColor = "00BBBB";

    @Config.Name("Button Color (Idle)")
    public static String idleButtonColor = "007777";
}
