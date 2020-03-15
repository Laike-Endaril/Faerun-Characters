package com.fantasticsource.faeruncharacters;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

@Config(modid = MODID)
public class FaerunCharactersConfig
{
    @Config.Name("Button Color (Active)")
    public static String activeButtonColor = "00FFFF";

    @Config.Name("Button Color (Hover)")
    public static String hoverButtonColor = "00BBBB";

    @Config.Name("Button Color (Idle)")
    public static String idleButtonColor = "007777";
}
