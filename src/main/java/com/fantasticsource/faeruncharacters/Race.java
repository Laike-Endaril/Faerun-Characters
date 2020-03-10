package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class Race
{
    public static final LinkedHashMap<String, Race> RACES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Race> RACES_PREMIUM = new LinkedHashMap<>();

    public static final String AW_SKIN_LIBRARY_DIR = MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator;


    public String name;

    //For skins, the strings are the library file paths (eg. official/Arbalest)
    //For colors, the alpha is paint type (255 is normal).  If a color hashset is set to null, then it's treated as a full-range HSV slider

    //Hidden
    public boolean renderLegArmor = true, renderFootArmor = true, renderCape = true, skinColorSetsHairColor = false;
    public Color bloodColor = new Color(255, 0, 0, 255);


    //Body
    public HashSet<String> raceVariants = new HashSet<>(), premiumRaceVariants = new HashSet<>(), tails = new HashSet<>();
    public HashSet<Color> skinColors = new HashSet<>();
    public HashSet<String> chestSizes = new HashSet<>();
    public double renderScaleMin = 1, renderScaleMax = 1;


    //Head
    public HashSet<String> hairBase = new HashSet<>(), premiumHairBase = new HashSet<>();
    public HashSet<String> hairFront = new HashSet<>(), premiumHairFront = new HashSet<>();
    public HashSet<String> hairBack = new HashSet<>(), premiumHairBack = new HashSet<>();
    public HashSet<String> hairTop = new HashSet<>(), premiumHairTop = new HashSet<>();
    public HashSet<Color> hairColors = new HashSet<>();

    public HashSet<String> eyes = new HashSet<>(), premiumEyes = new HashSet<>();
    public HashSet<Color> eyeColors = new HashSet<>();


    //Other
//  TODO Voice = Voice/Normal
//  TODO Voice pitch = 0.8 to 1.2


    protected boolean valid()
    {
        //Body
        if (raceVariants.size() + premiumRaceVariants.size() == 0)
        {
            System.err.println("No variants found for race: " + name);
            return false;
        }
        if (skinColors != null && skinColors.size() == 0)
        {
            System.err.println("No skin colors found for race: " + name);
            return false;
        }
        if (chestSizes.size() == 0)
        {
            System.err.println("No chest sizes found for race: " + name);
            return false;
        }

        //Head
        if (!skinColorSetsHairColor && hairColors != null && hairColors.size() == 0)
        {
            for (HashSet<String> hairSet : new HashSet[]{hairBase, premiumHairBase, hairFront, premiumHairFront, hairBack, premiumHairBack, hairTop, premiumHairTop})
            {
                if (hairSet.size() > 0)
                {
                    System.err.println("Found hair, but no hair colors for race: " + name);
                    return false;
                }
            }
        }
        if (eyes.size() + premiumEyes.size() > 0 && eyeColors != null && eyeColors.size() == 0)
        {
            System.err.println("Found eyes, but no eye colors for race: " + name);
            return false;
        }

        //Other
        //TODO

        return true;
    }


    protected void setValue(String key, String value)
    {
        switch (key)
        {
            //Hidden
            case "render leg armor":
                renderLegArmor = Boolean.parseBoolean(value);
                break;

            case "render foot armor":
                renderFootArmor = Boolean.parseBoolean(value);
                break;

            case "render cape":
                renderCape = Boolean.parseBoolean(value);
                break;

            case "skin color sets hair color":
                skinColorSetsHairColor = Boolean.parseBoolean(value);
                break;

            case "blood color":
                bloodColor = new Color(Tools.parseHexInt(value));
                break;


            //Body
            case "race variant":
                loadSkins(raceVariants, Tools.fixedSplit(value, ","));
                break;

            case "premium race variant":
                loadSkins(premiumRaceVariants, Tools.fixedSplit(value, ","));
                break;

            case "tail":
                loadSkins(tails, Tools.fixedSplit(value, ","));
                break;

            case "skin color":
                skinColors = loadColors(skinColors, Tools.fixedSplit(value, ","));
                break;

            case "chest":
                for (String mode : Tools.fixedSplit(value, ","))
                {
                    chestSizes.add(mode.trim().toLowerCase());
                }
                break;

            case "scale":
                String[] tokens = Tools.fixedSplit(value, ",");
                if (tokens.length == 2)
                {
                    renderScaleMin = Double.parseDouble(tokens[0].trim());
                    renderScaleMax = Double.parseDouble(tokens[1].trim());
                }
                break;


            //Head
            case "hair":
                loadSkins(hairBase, Tools.fixedSplit(value, ","));
                break;

            case "premium hair":
                loadSkins(premiumHairBase, Tools.fixedSplit(value, ","));
                break;

            case "hair front":
                loadSkins(hairFront, Tools.fixedSplit(value, ","));
                break;

            case "premium hair front":
                loadSkins(premiumHairFront, Tools.fixedSplit(value, ","));
                break;

            case "hair back":
                loadSkins(hairBack, Tools.fixedSplit(value, ","));
                break;

            case "premium hair back":
                loadSkins(premiumHairBack, Tools.fixedSplit(value, ","));
                break;

            case "hair top":
                loadSkins(hairTop, Tools.fixedSplit(value, ","));
                break;

            case "premium hair top":
                loadSkins(premiumHairTop, Tools.fixedSplit(value, ","));
                break;

            case "hair color":
                hairColors = loadColors(hairColors, Tools.fixedSplit(value, ","));
                break;

            case "eyes":
                loadSkins(eyes, Tools.fixedSplit(value, ","));
                break;

            case "premium eyes":
                loadSkins(premiumEyes, Tools.fixedSplit(value, ","));
                break;

            case "eye color":
                eyeColors = loadColors(eyeColors, Tools.fixedSplit(value, ","));
                break;


            //Other
            //TODO


            default:
                System.err.println("Unknown key/value pair: <" + key + ">, <" + value + ">\nFor race: " + name);
        }
    }

    protected HashSet<Color> loadColors(HashSet<Color> colorSet, String[] colorStrings)
    {
        if (colorSet == null) return null;

        for (String colorString : colorStrings)
        {
            colorString = colorString.trim();
            if (colorString.toLowerCase().equals("any")) return null;

            colorSet.add(new Color(Tools.parseHexInt(colorString)));
        }

        return colorSet;
    }

    protected void loadSkins(HashSet<String> skinSet, String[] skinStrings)
    {
        boolean pool = false;
        for (String skinString : skinStrings)
        {
            if (skinString.substring(0, 7).equals("folder:"))
            {
                pool = true;
                skinString = skinString.replace("folder:", "");
            }

            skinString = Tools.fixFileSeparators(skinString.trim());

            File skinFile = new File(AW_SKIN_LIBRARY_DIR + skinString);

            if (!pool)
            {
                if (!skinFile.exists()) skinFile = new File(AW_SKIN_LIBRARY_DIR + skinString + ".armour");

                if (!skinFile.exists()) continue;

                skinSet.add(skinString);
            }
            else
            {
                if (!skinFile.isDirectory()) continue;

                File[] subFiles = skinFile.listFiles();
                if (subFiles == null || subFiles.length == 0) continue;


                String[] subSkinStrings = new String[subFiles.length];
                int i = 0;
                for (File subFile : subFiles)
                {
                    subSkinStrings[i++] = subFile.getAbsolutePath().replace(AW_SKIN_LIBRARY_DIR, "").replace(".armour", "");
                }

                loadSkins(skinSet, subSkinStrings);
            }
        }
    }


    public static void addRace(String name) throws IOException
    {
        Race race = new Race();
        race.name = name;


        //Load
        boolean premium = false;
        File file = new File(MCTools.getConfigDir() + MODID + File.separator + "races" + File.separator + name + ".txt");
        if (!file.exists() || file.isDirectory())
        {
            premium = true;
            file = new File(MCTools.getConfigDir() + MODID + File.separator + "racesPremium" + File.separator + name + ".txt");
        }
        if (!file.exists() || file.isDirectory()) return;

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null)
        {
            int commentIndex = line.indexOf('#');
            if (commentIndex != -1) line = line.substring(0, commentIndex);

            String[] tokens = Tools.fixedSplit(line, "=");
            if (tokens.length == 2)
            {
                race.setValue(tokens[0].trim().toLowerCase(), tokens[1].trim());
            }

            line = br.readLine();
        }


        //Validate
        if (!race.valid()) return;


        //Save and return
        if (premium) RACES_PREMIUM.put(name, race);
        else RACES.put(name, race);
    }


    public static void init(FMLPreInitializationEvent event) throws IOException
    {
        File file = new File(MCTools.getConfigDir() + MODID + File.separator + "races");
        if (!file.isDirectory()) return;

        File[] files = file.listFiles();
        if (files == null) return;

        for (File raceFile : files) addRace(raceFile.getName().replace(".txt", ""));
    }
}
