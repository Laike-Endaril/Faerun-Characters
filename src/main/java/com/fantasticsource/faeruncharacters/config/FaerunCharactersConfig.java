package com.fantasticsource.faeruncharacters.config;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

@Config(modid = MODID)
public class FaerunCharactersConfig
{
    @Config.Name("Client Configs")
    public static ClientConfig client = new ClientConfig();

    @Config.Name("Server Configs")
    public static ServerConfig server = new ServerConfig();
}
