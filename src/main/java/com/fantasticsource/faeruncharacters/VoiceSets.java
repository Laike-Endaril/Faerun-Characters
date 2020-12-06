package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class VoiceSets
{
    //voiceSetName, soundType, playableSoundRL
    public static final LinkedHashMap<String, LinkedHashMap<String, ResourceLocation>> ALL_VOICE_SETS = new LinkedHashMap<>();

    public static void init(FMLPreInitializationEvent event) throws IOException
    {
        File dir = new File(MCTools.getConfigDir() + MODID + File.separator + "voices");
        if (!dir.isDirectory())
        {
            dir.mkdirs();
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files)
        {
            if (!file.exists() || file.isDirectory()) return;

            LinkedHashMap<String, ResourceLocation> voiceMappings = new LinkedHashMap<>();
            ALL_VOICE_SETS.put(file.getName().replace(".txt", ""), voiceMappings);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null)
            {
                int commentIndex = line.indexOf('#');
                if (commentIndex != -1) line = line.substring(0, commentIndex);

                String[] tokens = Tools.fixedSplit(line, "=");
                if (tokens.length == 2)
                {
                    voiceMappings.put(tokens[0].trim(), new ResourceLocation(tokens[1].trim()));
                }

                line = br.readLine();
            }
            br.close();
        }
    }
}
