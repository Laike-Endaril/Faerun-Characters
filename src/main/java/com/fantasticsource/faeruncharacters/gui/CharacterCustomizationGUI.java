package com.fantasticsource.faeruncharacters.gui;

import com.fantasticsource.faeruncharacters.CCSounds;
import com.fantasticsource.faeruncharacters.CRace;
import com.fantasticsource.faeruncharacters.CharacterCustomization;
import com.fantasticsource.faeruncharacters.Network;
import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.entity.Camera;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.mctools.sound.SimpleSound;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class CharacterCustomizationGUI extends GUIScreen
{
    public static final int
            ELEMENT_W = 128, ELEMENT_H = 16,
            PADDING = 4,

    //Definitions for what we want to allow for
    TOTAL_W_LEFT = PADDING + ELEMENT_W + PADDING + ELEMENT_W,
            TOTAL_W_RIGHT = PADDING + ELEMENT_W * 2,
            TOTAL_H = ELEMENT_H * 15;

    public static final double
            W_PERCENT_LEFT = 0.4, W_PERCENT_RIGHT = 0.4, H_PERCENT = 1;


    public static final LinkedHashSet<String> bodyTypes = new LinkedHashSet<>();
    public static double internalScaling, buttonRelW, buttonRelH, paddingRelW, paddingRelH, sliderRelH;

    public static Color
            activeButtonColor = new Color(FaerunCharactersConfig.client.activeButtonColor, true),
            hoverButtonColor = new Color(FaerunCharactersConfig.client.hoverButtonColor, true),
            idleButtonColor = new Color(FaerunCharactersConfig.client.idleButtonColor, true),
            activePremiumButtonColor = new Color(FaerunCharactersConfig.client.activePremiumButtonColor, true),
            hoverPremiumButtonColor = new Color(FaerunCharactersConfig.client.hoverPremiumButtonColor, true),
            idlePremiumButtonColor = new Color(FaerunCharactersConfig.client.idlePremiumButtonColor, true);

    public static final ResourceLocation
            TEX_BUTTON_IDLE = new ResourceLocation(MODID, "image/button_idle.png"),
            TEX_BUTTON_HOVER = new ResourceLocation(MODID, "image/button_hover.png"),
            TEX_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/button_active.png"),
            TEX_PREMIUM_BUTTON_IDLE = new ResourceLocation(MODID, "image/button_premium_idle.png"),
            TEX_PREMIUM_BUTTON_HOVER = new ResourceLocation(MODID, "image/button_premium_hover.png"),
            TEX_PREMIUM_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/button_premium_active.png"),
            TEX_BUTTON_IDLE_ERROR = new ResourceLocation(MODID, "image/button_idle_error.png"),
            TEX_BUTTON_HOVER_ERROR = new ResourceLocation(MODID, "image/button_hover_error.png"),
            TEX_BUTTON_ACTIVE_ERROR = new ResourceLocation(MODID, "image/button_active_error.png"),
            TEX_PREMIUM_BUTTON_IDLE_ERROR = new ResourceLocation(MODID, "image/button_premium_idle_error.png"),
            TEX_PREMIUM_BUTTON_HOVER_ERROR = new ResourceLocation(MODID, "image/button_premium_hover_error.png"),
            TEX_PREMIUM_BUTTON_ACTIVE_ERROR = new ResourceLocation(MODID, "image/button_premium_active_error.png"),
            TEX_SLIDER_BAR = new ResourceLocation(MODID, "image/slider_bar.png"),
            TEX_SLIDER_KNOB = new ResourceLocation(MODID, "image/slider_knob.png"),
            TEX_SINGLE_COLOR = new ResourceLocation(MODID, "image/single_color.png"),
            TEX_SLIDER_HUE = new ResourceLocation(MODID, "image/slider_hue.png"),
            TEX_SLIDER_SATURATION = new ResourceLocation(MODID, "image/slider_saturation.png"),
            TEX_SLIDER_GRADIENT = new ResourceLocation(MODID, "image/slider_gradient.png");

    protected static final String[] TAB_NAMES = new String[]{"Body", "Head", "Accessories", "Other"};


    static
    {
        MinecraftForge.EVENT_BUS.register(CharacterCustomizationGUI.class);
        bodyTypes.add("Masculine");
        bodyTypes.add("Feminine");
    }


    public Network.CharacterCustomizationGUIPacket packet;
    public NBTTagCompound ccCompound;
    protected String selectedTab = "Body", selectedOption = "Race";
    protected ArrayList<String> options = new ArrayList<>();
    protected CRace race;
    protected HashSet<String> errors = new HashSet<>();
    protected GUICCCameraController root2;


    public CharacterCustomizationGUI(Network.CharacterCustomizationGUIPacket packet)
    {
        this.packet = packet;
        ccCompound = packet.ccCompound;

        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(this);

        root2 = addCamControls();
        refresh();

        //Set to camera view
        EntityPlayer player = mc.player;
        Camera.getCamera().activate(player.world, player.posX, player.posY + player.height / 2, player.posZ, 180, 0);
    }


    protected void preCalc()
    {
        //Highest possible internal scaling that fits within bounds
        int mcScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        internalScaling = Tools.min(W_PERCENT_LEFT * pxWidth / (TOTAL_W_LEFT * mcScale), W_PERCENT_RIGHT * pxWidth / (TOTAL_W_RIGHT * mcScale), H_PERCENT * pxHeight / (TOTAL_H * mcScale));

        //These two lines prioritize full-pixel scaling multiples
        double internalScaling2 = Math.floor(mcScale * internalScaling) / mcScale;
        if (internalScaling2 != 0) internalScaling = internalScaling2;


        //Calc relative element sizes
        buttonRelW = (double) ELEMENT_W * internalScaling * mcScale / pxWidth;
        buttonRelH = (double) ELEMENT_H * internalScaling * mcScale / pxHeight;

        paddingRelW = (double) PADDING * internalScaling * mcScale / pxWidth;
        paddingRelH = (double) PADDING * internalScaling * mcScale / pxHeight;

        sliderRelH = (double) ELEMENT_H * internalScaling * mcScale / pxHeight;
    }

    protected void calcErrors()
    {
        errors.clear();


        //Body
        if (packet.isPremium)
        {
            if (!packet.races.keySet().contains(ccCompound.getString("Race")) && !packet.racesPremium.keySet().contains(ccCompound.getString("Race")))
            {
                errors.add("Body");
                errors.add("Race");
            }
        }
        else
        {
            if (!packet.races.keySet().contains(ccCompound.getString("Race")))
            {
                errors.add("Body");
                errors.add("Race");
            }
        }


        CRace race = packet.races.get(ccCompound.getString("Race"));
        if (race == null) race = packet.racesPremium.get(ccCompound.getString("Race"));

        if (packet.isPremium)
        {
            if (!race.raceVariants.contains(ccCompound.getString("Race Variant")) && !race.premiumRaceVariants.contains(ccCompound.getString("Race Variant")))
            {
                errors.add("Body");
                errors.add("Race Variant");
            }
        }
        else
        {
            if (!race.raceVariants.contains(ccCompound.getString("Race Variant")))
            {
                errors.add("Body");
                errors.add("Race Variant");
            }
        }


        //Head
        if (packet.isPremium)
        {
            if (!race.hairBase.contains(ccCompound.getString("Hair (Base)")) && !race.premiumHairBase.contains(ccCompound.getString("Hair (Base)")))
            {
                errors.add("Head");
                errors.add("Hair (Base)");
            }
        }
        else
        {
            if (!race.hairBase.contains(ccCompound.getString("Hair (Base)")))
            {
                errors.add("Head");
                errors.add("Hair (Base)");
            }
        }

        if (packet.isPremium)
        {
            if (!race.hairFront.contains(ccCompound.getString("Hair (Front)")) && !race.premiumHairFront.contains(ccCompound.getString("Hair (Front)")))
            {
                errors.add("Head");
                errors.add("Hair (Front)");
            }
        }
        else
        {
            if (!race.hairFront.contains(ccCompound.getString("Hair (Front)")))
            {
                errors.add("Head");
                errors.add("Hair (Front)");
            }
        }

        if (packet.isPremium)
        {
            if (!race.hairBack.contains(ccCompound.getString("Hair (Back)")) && !race.premiumHairBack.contains(ccCompound.getString("Hair (Back)")))
            {
                errors.add("Head");
                errors.add("Hair (Back)");
            }
        }
        else
        {
            if (!race.hairBack.contains(ccCompound.getString("Hair (Back)")))
            {
                errors.add("Head");
                errors.add("Hair (Back)");
            }
        }

        if (packet.isPremium)
        {
            if (!race.hairTop.contains(ccCompound.getString("Hair (Top/Overall 1)")) && !race.premiumHairTop.contains(ccCompound.getString("Hair (Top/Overall 1)")))
            {
                errors.add("Head");
                errors.add("Hair (Top/Overall 1)");
            }
        }
        else
        {
            if (!race.hairTop.contains(ccCompound.getString("Hair (Top/Overall 1)")))
            {
                errors.add("Head");
                errors.add("Hair (Top/Overall 1)");
            }
        }

        if (packet.isPremium)
        {
            if (!race.hairTop.contains(ccCompound.getString("Hair (Top/Overall 2)")) && !race.premiumHairTop.contains(ccCompound.getString("Hair (Top/Overall 2)")))
            {
                errors.add("Head");
                errors.add("Hair (Top/Overall 2)");
            }
        }
        else
        {
            if (!race.hairTop.contains(ccCompound.getString("Hair (Top/Overall 2)")))
            {
                errors.add("Head");
                errors.add("Hair (Top/Overall 2)");
            }
        }

        if (packet.isPremium)
        {
            if (!race.eyes.contains(ccCompound.getString("Eyes")) && !race.premiumEyes.contains(ccCompound.getString("Eyes")))
            {
                errors.add("Head");
                errors.add("Eyes");
            }
        }
        else
        {
            if (!race.eyes.contains(ccCompound.getString("Eyes")))
            {
                errors.add("Head");
                errors.add("Eyes");
            }
        }


        //Other
        if (race.voiceSets.size() + race.premiumVoiceSets.size() > 0)
        {
            if (packet.isPremium)
            {
                if (!race.voiceSets.contains(ccCompound.getString("Voice")) && !race.premiumVoiceSets.contains(ccCompound.getString("Voice")))
                {
                    errors.add("Other");
                    errors.add("Voice");
                }
            }
            else
            {
                if (!race.voiceSets.contains(ccCompound.getString("Voice")))
                {
                    errors.add("Other");
                    errors.add("Voice");
                }
            }
        }
    }

    public void refresh()
    {
        preCalc();
        calcErrors();

        String raceString = ccCompound.getString("Race");
        race = packet.races.get(raceString);
        if (race == null) race = packet.racesPremium.get(raceString);

        root2.clear();
        addTabsAndDoneButton(root2);
        addOptions(root2);
        addOptionControls(root2);
    }


    protected GUICCCameraController addCamControls()
    {
        GUICCCameraController result = new GUICCCameraController(this, 0, 0, 1, 1);
        root.add(result);
        return result;
    }


    protected void addTabsAndDoneButton(GUIElement root2)
    {
        //Tab buttons
        double yy = (1 - buttonRelH * TAB_NAMES.length) / 2;
        for (int i = 0; i < TAB_NAMES.length; i++)
        {
            String tabName = TAB_NAMES[i];
            GUIButton button = makeButton(paddingRelW, yy, tabName, errors.contains(tabName));
            button.addClickActions(() ->
            {
                SimpleSound.play(CCSounds.CLICK);
                selectedTab = tabName;
                recalc();
            });
            if (tabName.equals(selectedTab)) button.setActive(true);

            root2.add(button);
            yy += buttonRelH;
        }


        //Done button
        GUIButton button = makeButton(paddingRelW, 1 - paddingRelH - buttonRelH, "Done", errors.size() > 0);
        button.addClickActions(() -> Network.WRAPPER.sendToServer(new Network.LeaveCCPacket()));

        root2.add(button);
    }


    protected void addOptions(GUIElement root2)
    {
        options.clear();


        switch (selectedTab)
        {
            case "Body":
                if (packet.races.size() + packet.racesPremium.size() > 1) options.add("Race");
                if (race.raceVariants.size() + race.premiumRaceVariants.size() > 1) options.add("Race Variant");
                if (race.tails.size() > 1) options.add("Tail");
                if (packet.bareArms.size() > 1) options.add("Bare Arms");
                if (race.skinColors == null || race.skinColors.size() > 1) options.add("Skin Color");
                options.add("Body Type");
                if (race.chestSizes.size() > 1) options.add("Chest");
                options.add("Scale");
                options.add("Undershirt Color");
                break;


            case "Head":
                if (race.hairBase.size() + race.premiumHairBase.size() > 1) options.add("Hair (Base)");
                if (race.hairFront.size() + race.premiumHairFront.size() > 1) options.add("Hair (Front)");
                if (race.hairBack.size() + race.premiumHairBack.size() > 1) options.add("Hair (Back)");
                if (race.hairTop.size() + race.premiumHairTop.size() > 1)
                {
                    options.add("Hair (Top/Overall 1)");
                    options.add("Hair (Top/Overall 2)");
                }
                if (race.skinColorSetsHairColor)
                {
                    if (race.skinColors == null || race.skinColors.size() > 1) options.add("Hair Color");
                }
                else
                {
                    if (race.hairColors == null || race.hairColors.size() > 1) options.add("Hair Color");
                }
                if (race.eyes.size() > 1) options.add("Eyes");
                if (race.eyeColors == null || race.eyeColors.size() > 1) options.add("Eye Color");
                break;


            case "Accessories":
                if (packet.markings.size() > 1) options.add("Markings");
                if (packet.headAccessories.size() > 1) options.add("Accessory (Head)");
                if (packet.faceAccessories.size() > 1) options.add("Accessory (Face)");
                options.add("Color 1");
                options.add("Color 2");
                break;


            case "Other":
                if (race.voiceSets.size() + race.premiumVoiceSets.size() > 1) options.add("Voice");
                if (race.voiceSets.size() + race.premiumVoiceSets.size() > 0) options.add("Voice Pitch");
                break;
        }

        if (!options.contains(selectedOption)) selectedOption = options.get(0);


        double yy = (1 - buttonRelH * options.size()) / 2;
        for (int i = 0; i < options.size(); i++)
        {
            String optionName = options.get(i);
            GUIButton button = makeButton(paddingRelW + buttonRelW + paddingRelW, yy, optionName, errors.contains(optionName));
            button.addClickActions(() ->
            {
                SimpleSound.play(CCSounds.CLICK);
                selectedOption = optionName;
                recalc();
            });
            if (optionName.equals(selectedOption)) button.setActive(true);

            root2.add(button);
            yy += buttonRelH;
        }
    }


    protected void addOptionControls(GUIElement root2)
    {
        switch (selectedTab)
        {
            case "Body":
                switch (selectedOption)
                {
                    //String selectors
                    case "Race":
                        addStringSelector(root2, selectedOption, true, packet.races.keySet(), packet.racesPremium.keySet());
                        break;

                    case "Race Variant":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.raceVariants, race.premiumRaceVariants);
                        break;

                    case "Tail":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.tails);
                        break;

                    case "Bare Arms":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, packet.bareArms);
                        break;

                    case "Body Type":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, false, bodyTypes);
                        break;

                    case "Chest":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, false, race.chestSizes);
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Skin Color":
                        if (race == null) break;
                        if (race.skinColors != null) addColorSelector(root2, selectedOption, race.skinColors.toArray(new Color[0]));
                        else addHSVSliders(root2, selectedOption);
                        break;

                    case "Undershirt Color":
                        if (race == null) break;
                        addHSVSliders(root2, selectedOption);
                        break;


                    //Sliders
                    case "Scale":
                        if (race == null) break;
                        addSingleSliderDouble(root2, selectedOption, race.renderScaleMin, race.renderScaleMax);
                        break;
                }
                break;


            case "Head":
                switch (selectedOption)
                {
                    //String selectors
                    case "Hair (Base)":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.hairBase, race.premiumHairBase);
                        break;

                    case "Hair (Front)":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.hairFront, race.premiumHairFront);
                        break;

                    case "Hair (Back)":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.hairBack, race.premiumHairBack);
                        break;

                    case "Hair (Top/Overall 1)":
                    case "Hair (Top/Overall 2)":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.hairTop, race.premiumHairTop);
                        break;

                    case "Eyes":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, race.eyes, race.premiumEyes);
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Hair Color":
                        if (race == null) break;
                        if (race.skinColorSetsHairColor)
                        {
                            if (race.skinColors != null) addColorSelector(root2, "Skin Color", race.skinColors.toArray(new Color[0]));
                            else addHSVSliders(root2, "Skin Color");
                        }
                        else
                        {
                            if (race.hairColors != null) addColorSelector(root2, selectedOption, race.hairColors.toArray(new Color[0]));
                            else addHSVSliders(root2, selectedOption);
                        }
                        break;

                    case "Eye Color":
                        if (race == null) break;
                        if (race.eyeColors != null) addColorSelector(root2, selectedOption, race.eyeColors.toArray(new Color[0]));
                        else addHSVSliders(root2, selectedOption);
                        break;
                }
                break;


            case "Accessories":
                switch (selectedOption)
                {
                    //String selectors
                    case "Markings":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, packet.markings);
                        break;

                    case "Accessory (Head)":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, packet.headAccessories);
                        break;

                    case "Accessory (Face)":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, true, packet.faceAccessories);
                        break;


                    //Color selectors or HSV sliders, depending on race
                    case "Color 1":
                        if (race == null) break;
                        addHSVSliders(root2, selectedOption);
                        break;

                    case "Color 2":
                        if (race == null) break;
                        addHSVSliders(root2, selectedOption);
                        break;
                }
                break;

            case "Other":

            case "Voice":
                switch (selectedOption)
                {
                    //String selectors
                    case "Voice":
                        if (race == null) break;
                        addStringSelector(root2, selectedOption, false, race.voiceSets, race.premiumVoiceSets);
                        break;


                    //Sliders
                    case "Voice Pitch":
                        if (race == null) break;
                        addSingleSliderDouble(root2, selectedOption, race.pitchMin, race.pitchMax);
                        break;
                }
                break;
        }
    }


    protected void addStringSelector(GUIElement root2, String key, boolean fileNames, Collection<String> selections)
    {
        addStringSelector(root2, key, fileNames, selections, new LinkedHashSet<>());
    }

    protected void addStringSelector(GUIElement root2, String key, boolean fileNames, Collection<String> selections, Collection<String> premiumSelections)
    {
        String current;
        switch (key)
        {
            case "Body Type":
                current = packet.bodyType;
                break;

            case "Chest":
                current = packet.chest;
                break;

            default:
                current = ccCompound.getString(key);
        }

        ArrayList<String> options = new ArrayList<>();
        options.addAll(selections);
        options.addAll(premiumSelections);


        double yy = (1 - buttonRelH * Math.ceil(options.size() / 2d)) / 2;
        for (int i = 0; i < options.size(); i++)
        {
            String buttonText = options.get(i);
            String buttonShortText = buttonText;
            if (fileNames)
            {
                buttonShortText = Tools.fixFileSeparators(buttonShortText);
                buttonShortText = buttonShortText.substring(buttonShortText.lastIndexOf(File.separator) + 1);
            }

            GUIButton button = makeButton(i % 2 == 0 ? 1 - paddingRelW - buttonRelW * 2 : 1 - paddingRelW - buttonRelW, yy, buttonShortText, !packet.isPremium && i >= selections.size(), i >= selections.size());
            button.addClickActions(() ->
            {
                SimpleSound.play(CCSounds.CLICK);
                if (!buttonText.equals(current))
                {
                    switch (key)
                    {
                        case "Body Type":
                            packet.bodyType = buttonText;
                            Network.WRAPPER.sendToServer(new Network.SetBodyTypePacket(buttonText.equals("Masculine") ? "M" : "F"));
                            break;

                        case "Chest":
                            packet.chest = buttonText;
                            Network.WRAPPER.sendToServer(new Network.SetChestTypePacket(buttonText));
                            break;

                        case "Voice":
                            ccCompound.setString(key, buttonText);
                            Network.WRAPPER.sendToServer(new Network.SetCCStringPacket(key, buttonText));
                            break;

                        default:
                            ccCompound.setString(key, buttonText);
                            Network.WRAPPER.sendToServer(new Network.SetCCSkinPacket(key, buttonText));
                            break;
                    }

                    recalc();
                }
            });
            if (buttonText.equals(current)) button.setActive(true);

            root2.add(button);
            if (i % 2 == 1) yy += buttonRelH;
        }
    }


    protected void addSingleSliderDouble(GUIElement root2, String key, double min, double max)
    {
        GUIHorizontalSlider slider = new GUIHorizontalSlider(this, 1 - paddingRelW - buttonRelW, (1 - sliderRelH) / 2, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, min, max, TEX_SLIDER_BAR, TEX_SLIDER_KNOB);
        slider.setValue(ccCompound.getDouble(key));
        slider.addDragActions(() ->
        {
            ccCompound.setDouble(key, slider.getValue());
            Network.WRAPPER.sendToServer(new Network.SetCCDoublePacket(key, slider.getValue()));
        });

        root2.add(slider);
    }


    protected void addColorSelector(GUIElement root2, String key, Color... colors)
    {
        Color current = new Color(ccCompound.getInteger(key));


        double yy = (1 - buttonRelH * Math.ceil(colors.length / 2d)) / 2;
        for (int i = 0; i < colors.length; i++)
        {
            Color buttonColor = colors[i];

            GUIButton button = makeColorButton(i % 2 == 0 ? 1 - paddingRelW - buttonRelW * 2 : 1 - paddingRelW - buttonRelW, yy, buttonColor);
            button.addClickActions(() ->
            {
                SimpleSound.play(CCSounds.CLICK);
                if (buttonColor.equals(current)) ccCompound.removeTag(key);
                else ccCompound.setInteger(key, buttonColor.color());
                Network.WRAPPER.sendToServer(new Network.SetCCColorPacket(key, buttonColor.color()));

                recalc();
            });
            if (buttonColor.equals(current)) button.setActive(true);

            root2.add(button);
            if (i % 2 == 1) yy += buttonRelH;
        }
    }


    protected void addHSVSliders(GUIElement root2, String key)
    {
        Color color = new Color(ccCompound.getInteger(key));

        GUIHorizontalSlider hueSlider = new GUIHorizontalSlider(this, 1 - paddingRelW - buttonRelW, (1 - sliderRelH) / 2 - sliderRelH, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, 0, 255, TEX_SLIDER_HUE, TEX_SLIDER_KNOB);
        hueSlider.setValue(color.h());

        GUIHorizontalSlider satSlider = new GUIHorizontalSlider(this, 1 - paddingRelW - buttonRelW, (1 - sliderRelH) / 2, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, 0, 255, TEX_SLIDER_SATURATION, TEX_SLIDER_KNOB);
        satSlider.setValue(color.s());
        GUIImage satOverlay = new GUIImage(this, 0, 0, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SLIDER_GRADIENT, new Color(0).setColorHSV(color.h(), 255, 255, 255));
        satSlider.add(0, satOverlay);

        GUIHorizontalSlider valSlider = new GUIHorizontalSlider(this, 1 - paddingRelW - buttonRelW, (1 - sliderRelH) / 2 + sliderRelH, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, 0, 255, TEX_BUTTON_ACTIVE, TEX_SLIDER_KNOB);
        valSlider.setValue(color.v());
        GUIImage valOverlay = new GUIImage(this, 0, 0, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SLIDER_GRADIENT, new Color(0).setColorHSV(color.h(), color.s(), 255, 255));
        valSlider.add(0, valOverlay);

        hueSlider.addDragActions(() ->
        {
            Color c = new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), (int) valSlider.getValue(), color.a());
            satOverlay.setColor(new Color(0).setColorHSV((int) hueSlider.getValue(), 255, 255, 255));
            valOverlay.setColor(new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), 255, 255));
            ccCompound.setInteger(key, c.color());
            Network.WRAPPER.sendToServer(new Network.SetCCColorPacket(key, c.color()));
        });

        satSlider.addDragActions(() ->
        {
            Color c = new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), (int) valSlider.getValue(), color.a());
            valOverlay.setColor(new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), 255, 255));
            ccCompound.setInteger(key, c.color());
            Network.WRAPPER.sendToServer(new Network.SetCCColorPacket(key, c.color()));
        });

        valSlider.addDragActions(() ->
        {
            Color c = new Color(0).setColorHSV((int) hueSlider.getValue(), (int) satSlider.getValue(), (int) valSlider.getValue(), color.a());
            ccCompound.setInteger(key, c.color());
            Network.WRAPPER.sendToServer(new Network.SetCCColorPacket(key, c.color()));
        });

        root2.addAll(hueSlider, satSlider, valSlider);
    }


    protected GUIButton makeButton(double x, double y, String text, boolean error)
    {
        return makeButton(x, y, text, error, false);
    }

    protected GUIButton makeButton(double x, double y, String text, boolean error, boolean premium)
    {
        if (error)
        {
            GUIImage active = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_ACTIVE_ERROR : TEX_BUTTON_ACTIVE_ERROR);
            active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
            active.add(new GUIText(this, text, premium ? activePremiumButtonColor : activeButtonColor, internalScaling));

            GUIImage hover = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_HOVER_ERROR : TEX_BUTTON_HOVER_ERROR);
            hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
            hover.add(new GUIText(this, text, premium ? hoverPremiumButtonColor : hoverButtonColor, internalScaling));

            GUIImage idle = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_IDLE_ERROR : TEX_BUTTON_IDLE_ERROR);
            idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
            idle.add(new GUIText(this, text, premium ? idlePremiumButtonColor : idleButtonColor, internalScaling));

            return new GUIButton(this, x, y, idle, hover, active, true);
        }
        else
        {
            GUIImage active = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_ACTIVE : TEX_BUTTON_ACTIVE);
            active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
            active.add(new GUIText(this, text, premium ? activePremiumButtonColor : activeButtonColor, internalScaling));

            GUIImage hover = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_HOVER : TEX_BUTTON_HOVER);
            hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
            hover.add(new GUIText(this, text, premium ? hoverPremiumButtonColor : hoverButtonColor, internalScaling));

            GUIImage idle = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, premium ? TEX_PREMIUM_BUTTON_IDLE : TEX_BUTTON_IDLE);
            idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
            idle.add(new GUIText(this, text, premium ? idlePremiumButtonColor : idleButtonColor, internalScaling));

            return new GUIButton(this, x, y, idle, hover, active, true);
        }
    }


    protected GUIButton makeColorButton(double x, double y, Color color)
    {
        GUIImage active = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SINGLE_COLOR, color));

        GUIImage hover = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SINGLE_COLOR, color));

        GUIImage idle = new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIImage(this, ELEMENT_W * internalScaling, ELEMENT_H * internalScaling, TEX_SINGLE_COLOR, color));

        return new GUIButton(this, x, y, idle, hover, active, true);
    }


    @Override
    protected void recalc()
    {
        super.recalc();

        refresh();

        root.recalc(0);
    }


    @Override
    public String title()
    {
        return "Character Customization";
    }


    @Override
    public void onClosed()
    {
        super.onClosed();
        if (Minecraft.getMinecraft().world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
        {
            Minecraft.getMinecraft().displayGuiScreen(this);
        }
        else Camera.getCamera().deactivate();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
