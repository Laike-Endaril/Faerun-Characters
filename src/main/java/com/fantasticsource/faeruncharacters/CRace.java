package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CRace extends Component
{
    public static final LinkedHashMap<String, CRace> RACES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, CRace> RACES_PREMIUM = new LinkedHashMap<>();

    public String name;

    //For skins, the strings are the library file paths (eg. official/Arbalest)
    //For colors, the alpha is paint type (255 is normal).  If a color hashset is set to null, then it's treated as a full-range HSV slider

    //Hidden
    public boolean renderLegArmor = true, renderFootArmor = true, renderCape = true, skinColorSetsHairColor = false;
    public Color bloodColor = new Color(255, 0, 0, 255);


    //Body
    public LinkedHashSet<String> raceVariants = new LinkedHashSet<>(), premiumRaceVariants = new LinkedHashSet<>(), tails = new LinkedHashSet<>(), defaultTorsos = new LinkedHashSet<>(), defaultChests = new LinkedHashSet<>();
    public LinkedHashSet<Color> skinColors = new LinkedHashSet<>();
    public LinkedHashSet<String> chestSizes = new LinkedHashSet<>();
    public double renderScaleMin = 1, renderScaleMax = 1;


    //Head
    public LinkedHashSet<String> hairBase = new LinkedHashSet<>(), premiumHairBase = new LinkedHashSet<>();
    public LinkedHashSet<String> hairFront = new LinkedHashSet<>(), premiumHairFront = new LinkedHashSet<>();
    public LinkedHashSet<String> hairBack = new LinkedHashSet<>(), premiumHairBack = new LinkedHashSet<>();
    public LinkedHashSet<String> hairTop = new LinkedHashSet<>(), premiumHairTop = new LinkedHashSet<>();
    public LinkedHashSet<Color> hairColors = new LinkedHashSet<>();

    public LinkedHashSet<String> eyes = new LinkedHashSet<>(), premiumEyes = new LinkedHashSet<>();
    public LinkedHashSet<Color> eyeColors = new LinkedHashSet<>();


    //Other
    public LinkedHashSet<String> voiceSets = new LinkedHashSet<>(), premiumVoiceSets = new LinkedHashSet<>();
    public double pitchMin = 0.8, pitchMax = 1.2;

    public static boolean addRace(String name) throws IOException
    {
        CRace race = new CRace();
        race.name = name;


        //Load
        File file = new File(MCTools.getConfigDir() + MODID + File.separator + "races" + File.separator + name + ".txt");
        if (!file.exists() || file.isDirectory()) return false;

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
        br.close();


        //Validate
        if (!race.valid()) return false;


        //Save and return
        if (race.raceVariants.size() == 0) RACES_PREMIUM.put(name, race);
        else RACES.put(name, race);
        return true;
    }

    public static void init(FMLPreInitializationEvent event) throws IOException
    {
        File file = new File(MCTools.getConfigDir() + MODID + File.separator + "races");
        if (!file.isDirectory())
        {
            file.mkdirs();
            return;
        }

        File[] files = file.listFiles();
        if (files == null) return;

        int i = 0;
        for (File raceFile : files)
        {
            if (addRace(raceFile.getName().replace(".txt", ""))) i++;
        }
        System.out.println(TextFormatting.LIGHT_PURPLE + "Loaded " + i + " races");
    }

    protected boolean valid()
    {
        //Body
        if (raceVariants.size() + premiumRaceVariants.size() == 0)
        {
            System.err.println(TextFormatting.RED + "No variants found for race: " + name);
            return false;
        }
        if (skinColors != null && skinColors.size() == 0)
        {
            System.err.println(TextFormatting.RED + "No skin colors found for race: " + name);
            return false;
        }
        if (defaultTorsos.size() == 0)
        {
            System.err.println(TextFormatting.RED + "No default torsos found for race: " + name);
            return false;
        }
        if (defaultChests.size() == 0)
        {
            System.err.println(TextFormatting.RED + "No default chests found for race: " + name);
            return false;
        }
        if (chestSizes.size() == 0)
        {
            System.err.println(TextFormatting.RED + "No chest sizes found for race: " + name);
            return false;
        }

        //Head
        if (!skinColorSetsHairColor && hairColors != null && hairColors.size() == 0)
        {
            for (LinkedHashSet<String> hairSet : new LinkedHashSet[]{hairBase, premiumHairBase, hairFront, premiumHairFront, hairBack, premiumHairBack, hairTop, premiumHairTop})
            {
                if (hairSet.size() > 0)
                {
                    System.err.println(TextFormatting.RED + "Found hair, but no hair colors for race: " + name);
                    return false;
                }
            }
        }
        if (eyes.size() + premiumEyes.size() > 0 && eyeColors != null && eyeColors.size() == 0)
        {
            System.err.println(TextFormatting.RED + "Found eyes, but no eye colors for race: " + name);
            return false;
        }

        return true;
    }

    protected void setValue(String key, String value)
    {
        String[] tokens;
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
                FaerunCharacters.loadSkinNames(raceVariants, Tools.fixedSplit(value, ","), false);
                break;

            case "premium race variant":
                FaerunCharacters.loadSkinNames(premiumRaceVariants, Tools.fixedSplit(value, ","), false);
                break;

            case "tail":
                FaerunCharacters.loadSkinNames(tails, Tools.fixedSplit(value, ","), false);
                break;

            case "skin color":
                skinColors = loadColors(skinColors, Tools.fixedSplit(value, ","));
                break;

            case "default torso":
                FaerunCharacters.loadSkinNames(defaultTorsos, Tools.fixedSplit(value, ","), false);
                break;

            case "default chest":
                FaerunCharacters.loadSkinNames(defaultChests, Tools.fixedSplit(value, ","), false);
                break;

            case "chest":
                for (String mode : Tools.fixedSplit(value, ","))
                {
                    chestSizes.add(mode.trim());
                }
                break;

            case "scale":
                tokens = Tools.fixedSplit(value, ",");
                if (tokens.length == 2)
                {
                    renderScaleMin = Double.parseDouble(tokens[0].trim());
                    renderScaleMax = Double.parseDouble(tokens[1].trim());
                }
                break;


            //Head
            case "hair":
                FaerunCharacters.loadSkinNames(hairBase, Tools.fixedSplit(value, ","), true);
                break;

            case "premium hair":
                FaerunCharacters.loadSkinNames(premiumHairBase, Tools.fixedSplit(value, ","), false);
                break;

            case "hair front":
                FaerunCharacters.loadSkinNames(hairFront, Tools.fixedSplit(value, ","), true);
                break;

            case "premium hair front":
                FaerunCharacters.loadSkinNames(premiumHairFront, Tools.fixedSplit(value, ","), false);
                break;

            case "hair back":
                FaerunCharacters.loadSkinNames(hairBack, Tools.fixedSplit(value, ","), true);
                break;

            case "premium hair back":
                FaerunCharacters.loadSkinNames(premiumHairBack, Tools.fixedSplit(value, ","), false);
                break;

            case "hair top":
                FaerunCharacters.loadSkinNames(hairTop, Tools.fixedSplit(value, ","), true);
                break;

            case "premium hair top":
                FaerunCharacters.loadSkinNames(premiumHairTop, Tools.fixedSplit(value, ","), false);
                break;

            case "hair color":
                hairColors = loadColors(hairColors, Tools.fixedSplit(value, ","));
                break;

            case "eyes":
                FaerunCharacters.loadSkinNames(eyes, Tools.fixedSplit(value, ","), false);
                break;

            case "premium eyes":
                FaerunCharacters.loadSkinNames(premiumEyes, Tools.fixedSplit(value, ","), false);
                break;

            case "eye color":
                eyeColors = loadColors(eyeColors, Tools.fixedSplit(value, ","));
                break;


            //Other
            case "voice set":
                for (String s : Tools.fixedSplit(value, ",")) voiceSets.add(s.trim());
                break;

            case "premium voice set":
                for (String s : Tools.fixedSplit(value, ",")) premiumVoiceSets.add(s.trim());
                break;

            case "voice pitch":
                tokens = Tools.fixedSplit(value, ",");
                if (tokens.length == 2)
                {
                    pitchMin = Double.parseDouble(tokens[0].trim());
                    pitchMax = Double.parseDouble(tokens[1].trim());
                }
                break;


            default:
                System.err.println(TextFormatting.RED + "Unknown key/value pair: <" + key + ">, <" + value + ">\nFor race: " + name);
        }
    }

    protected LinkedHashSet<Color> loadColors(LinkedHashSet<Color> colorSet, String[] colorStrings)
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

    @Override
    public CRace write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);


        //Hidden
        buf.writeBoolean(renderLegArmor);
        buf.writeBoolean(renderFootArmor);
        buf.writeBoolean(renderCape);
        buf.writeBoolean(skinColorSetsHairColor);
        buf.writeInt(bloodColor.color());


        //Body
        buf.writeInt(raceVariants.size());
        for (String s : raceVariants) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(premiumRaceVariants.size());
        for (String s : premiumRaceVariants) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(tails.size());
        for (String s : tails) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeBoolean(skinColors == null);
        if (skinColors != null)
        {
            buf.writeInt(skinColors.size());
            for (Color c : skinColors) buf.writeInt(c.color());
        }

        buf.writeInt(defaultTorsos.size());
        for (String s : defaultTorsos) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(defaultChests.size());
        for (String s : defaultChests) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(chestSizes.size());
        for (String s : chestSizes) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeDouble(renderScaleMin);
        buf.writeDouble(renderScaleMax);


        //Head
        buf.writeInt(hairBase.size());
        for (String s : hairBase) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(premiumHairBase.size());
        for (String s : premiumHairBase) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(hairFront.size());
        for (String s : hairFront) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(premiumHairFront.size());
        for (String s : premiumHairFront) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(hairBack.size());
        for (String s : hairBack) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(premiumHairBack.size());
        for (String s : premiumHairBack) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(hairTop.size());
        for (String s : hairTop) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(premiumHairTop.size());
        for (String s : premiumHairTop) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeBoolean(hairColors == null);
        if (hairColors != null)
        {
            buf.writeInt(hairColors.size());
            for (Color c : hairColors) buf.writeInt(c.color());
        }

        buf.writeInt(eyes.size());
        for (String s : eyes) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeInt(premiumEyes.size());
        for (String s : premiumEyes) ByteBufUtils.writeUTF8String(buf, s);

        buf.writeBoolean(eyeColors == null);
        if (eyeColors != null)
        {
            buf.writeInt(eyeColors.size());
            for (Color c : eyeColors) buf.writeInt(c.color());
        }


        //Other
        buf.writeDouble(pitchMin);
        buf.writeDouble(pitchMax);


        return this;
    }


    @Override
    public CRace read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);


        //Hidden
        renderLegArmor = buf.readBoolean();
        renderFootArmor = buf.readBoolean();
        renderCape = buf.readBoolean();
        skinColorSetsHairColor = buf.readBoolean();
        bloodColor = new Color(buf.readInt());


        //Body
        raceVariants.clear();
        for (int i = buf.readInt(); i > 0; i--) raceVariants.add(ByteBufUtils.readUTF8String(buf));

        premiumRaceVariants.clear();
        for (int i = buf.readInt(); i > 0; i--) premiumRaceVariants.add(ByteBufUtils.readUTF8String(buf));

        tails.clear();
        for (int i = buf.readInt(); i > 0; i--) tails.add(ByteBufUtils.readUTF8String(buf));

        if (buf.readBoolean()) skinColors = null;
        else
        {
            skinColors = new LinkedHashSet<>();
            for (int i = buf.readInt(); i > 0; i--) skinColors.add(new Color(buf.readInt()));
        }

        defaultTorsos.clear();
        for (int i = buf.readInt(); i > 0; i--) defaultTorsos.add(ByteBufUtils.readUTF8String(buf));

        defaultChests.clear();
        for (int i = buf.readInt(); i > 0; i--) defaultChests.add(ByteBufUtils.readUTF8String(buf));

        chestSizes.clear();
        for (int i = buf.readInt(); i > 0; i--) chestSizes.add(ByteBufUtils.readUTF8String(buf));

        renderScaleMin = buf.readDouble();
        renderScaleMax = buf.readDouble();


        //Head
        hairBase.clear();
        for (int i = buf.readInt(); i > 0; i--) hairBase.add(ByteBufUtils.readUTF8String(buf));

        premiumHairBase.clear();
        for (int i = buf.readInt(); i > 0; i--) premiumHairBase.add(ByteBufUtils.readUTF8String(buf));

        hairFront.clear();
        for (int i = buf.readInt(); i > 0; i--) hairFront.add(ByteBufUtils.readUTF8String(buf));

        premiumHairFront.clear();
        for (int i = buf.readInt(); i > 0; i--) premiumHairFront.add(ByteBufUtils.readUTF8String(buf));

        hairBack.clear();
        for (int i = buf.readInt(); i > 0; i--) hairBack.add(ByteBufUtils.readUTF8String(buf));

        premiumHairBack.clear();
        for (int i = buf.readInt(); i > 0; i--) premiumHairBack.add(ByteBufUtils.readUTF8String(buf));

        hairTop.clear();
        for (int i = buf.readInt(); i > 0; i--) hairTop.add(ByteBufUtils.readUTF8String(buf));

        premiumHairTop.clear();
        for (int i = buf.readInt(); i > 0; i--) premiumHairTop.add(ByteBufUtils.readUTF8String(buf));

        if (buf.readBoolean()) hairColors = null;
        else
        {
            hairColors = new LinkedHashSet<>();
            for (int i = buf.readInt(); i > 0; i--) hairColors.add(new Color(buf.readInt()));
        }

        eyes.clear();
        for (int i = buf.readInt(); i > 0; i--) eyes.add(ByteBufUtils.readUTF8String(buf));

        premiumEyes.clear();
        for (int i = buf.readInt(); i > 0; i--) premiumEyes.add(ByteBufUtils.readUTF8String(buf));

        if (buf.readBoolean()) eyeColors = null;
        else
        {
            eyeColors = new LinkedHashSet<>();
            for (int i = buf.readInt(); i > 0; i--) eyeColors.add(new Color(buf.readInt()));
        }


        //Other
        pitchMin = buf.readDouble();
        pitchMax = buf.readDouble();


        return this;
    }


    @Override
    public CRace save(OutputStream stream)
    {
        throw new NotImplementedException();
    }


    @Override
    public CRace load(InputStream stream)
    {
        throw new NotImplementedException();
    }
}
