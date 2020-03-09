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
    public HashSet<String> hair = new HashSet<>(), premiumHair = new HashSet<>();
    public HashSet<String> hairParts = new HashSet<>(), premiumHairParts = new HashSet<>();
    public HashSet<Color> hairColors = new HashSet<>();
    public HashSet<String> eyes = new HashSet<>(), premiumEyes = new HashSet<>();
    public HashSet<Color> eyeColors = new HashSet<>();

    //Accessories
    public HashSet<String> headAccessories = new HashSet<>(), premiumHeadAccessories = new HashSet<>();
    public HashSet<String> bodyAccessories = new HashSet<>(), premiumBodyAccessories = new HashSet<>();
    public HashSet<Color> accessoriesColors1 = new HashSet<>(), accessoriesColors2 = new HashSet<>();

    //Other
//  TODO Voice = Voice/Normal
//  TODO Voice pitch = 0.8 to 1.2


    protected boolean valid()
    {
        //Body
        if (raceVariants.size() + premiumRaceVariants.size() == 0) return false;
        if (skinColors != null && skinColors.size() == 0) return false;
        if (chestSizes.size() == 0) return false;

        //Head
        if (hair.size() + premiumHair.size() + hairParts.size() + premiumHairParts.size() > 0 && !skinColorSetsHairColor && hairColors != null && hairColors.size() == 0) return false;
        if (eyes.size() + premiumEyes.size() > 0 && eyeColors != null && eyeColors.size() == 0) return false;

        //Accessories
        if (headAccessories.size() + premiumHeadAccessories.size() + bodyAccessories.size() + premiumBodyAccessories.size() > 0 && ((accessoriesColors1 != null && accessoriesColors1.size() == 0) || (accessoriesColors2 != null && accessoriesColors2.size() == 0))) return false;

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
                loadSkins(hair, Tools.fixedSplit(value, ","));
                break;

            case "premium hair":
                loadSkins(premiumHair, Tools.fixedSplit(value, ","));
                break;

            case "hair parts":
                loadSkins(hairParts, Tools.fixedSplit(value, ","));
                break;

            case "premium hair parts":
                loadSkins(premiumHairParts, Tools.fixedSplit(value, ","));
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


            //Accessories
            case "head accessories":
                loadSkins(headAccessories, Tools.fixedSplit(value, ","));
                break;

            case "premium head accessories":
                loadSkins(premiumHeadAccessories, Tools.fixedSplit(value, ","));
                break;

            case "body accessories":
                loadSkins(bodyAccessories, Tools.fixedSplit(value, ","));
                break;

            case "premium body accessories":
                loadSkins(premiumBodyAccessories, Tools.fixedSplit(value, ","));
                break;

            case "accessories color 1":
                accessoriesColors1 = loadColors(accessoriesColors1, Tools.fixedSplit(value, ","));
                break;

            case "accessories color 2":
                accessoriesColors2 = loadColors(accessoriesColors2, Tools.fixedSplit(value, ","));
                break;


            //Other
            //TODO
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
        for (String skinString : skinStrings)
        {
            skinString = Tools.fixFileSeparators(skinString.trim());

            File skinFile = new File(AW_SKIN_LIBRARY_DIR + skinString);

            if (skinFile.isDirectory())
            {
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
            else skinSet.add(skinString);
        }
    }


    public static void addRace(String name) throws IOException
    {
        Race race = new Race();


        //Load
        File file = new File(MCTools.getConfigDir() + MODID + File.separator + "races" + File.separator + name + ".txt");
        if (!file.exists() || file.isDirectory()) return;

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null)
        {
            line = line.substring(0, line.indexOf('#'));
            String[] tokens = Tools.fixedSplit(line, "=");
            if (tokens.length == 2)
            {
                race.setValue(tokens[0].trim().toLowerCase(), tokens[1].trim());
            }

            line = br.readLine();
        }


        //Validate
        if (!race.valid())
        {
            System.err.println("Malformed race file: " + file.getAbsolutePath());
            return;
        }


        //Save and return
        RACES.put(name, race);
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
