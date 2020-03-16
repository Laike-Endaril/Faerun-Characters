package com.fantasticsource.faeruncharacters.config;

import net.minecraftforge.common.config.Config;

public class ClientConfig
{
    @Config.Name("Button Text Color (Active)")
    public String activeButtonColor = "FFFFFF";

    @Config.Name("Button Text Color (Hover)")
    public String hoverButtonColor = "BBBBBB";

    @Config.Name("Button Text Color (Idle)")
    public String idleButtonColor = "777777";

    @Config.Name("Premium Button Text Color (Active)")
    public String activePremiumButtonColor = "FFBB00";

    @Config.Name("Premium Button Text Color (Hover)")
    public String hoverPremiumButtonColor = "DDAA00";

    @Config.Name("Premium Button Text Color (Idle)")
    public String idlePremiumButtonColor = "BB7700";
}
