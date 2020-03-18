package com.fantasticsource.faeruncharacters.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;

public class GUIHorizontalSlider extends GUIImage
{
    protected double minValue, maxValue, value;
    protected ArrayList<Runnable> dragActions = new ArrayList<>();
    protected GUIImage knob;


    public GUIHorizontalSlider(GUIScreen screen, double unscaledWidth, double unscaledHeight, double minValue, double maxValue, ResourceLocation barTexture, ResourceLocation knobTexture)
    {
        super(screen, unscaledWidth, unscaledHeight, barTexture);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = minValue;

        knob = new GUIImage(screen, unscaledHeight, unscaledHeight, knobTexture);
        add(knob);
    }

    public GUIHorizontalSlider(GUIScreen screen, double unscaledWidth, double unscaledHeight, double minValue, double maxValue, ResourceLocation texture, ResourceLocation knobTexture, Color color)
    {
        super(screen, unscaledWidth, unscaledHeight, texture, color);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = minValue;

        knob = new GUIImage(screen, unscaledHeight, unscaledHeight, knobTexture, color);
        add(knob);
    }

    public GUIHorizontalSlider(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, double minValue, double maxValue, ResourceLocation texture, ResourceLocation knobTexture)
    {
        super(screen, x, y, unscaledWidth, unscaledHeight, texture);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = minValue;

        knob = new GUIImage(screen, unscaledHeight, unscaledHeight, knobTexture);
        add(knob);
    }

    public GUIHorizontalSlider(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, double minValue, double maxValue, ResourceLocation texture, ResourceLocation knobTexture, Color color)
    {
        super(screen, x, y, unscaledWidth, unscaledHeight, texture, color);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = minValue;

        knob = new GUIImage(screen, unscaledHeight, unscaledHeight, knobTexture, color);
        add(knob);
    }


    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        setActive(super.mousePressed(x, y, button));

        if (active)
        {
            setValueByMouseX();
            for (Runnable action : dragActions) action.run();
        }

        return active;
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        if (active && button == 0)
        {
            setValueByMouseX();
            for (Runnable action : dragActions) action.run();
        }
    }

    protected void setValueByMouseX()
    {
        double xx = absolutePxX(), ww = absolutePxWidth(), hh = absolutePxHeight();

        double x1 = xx + hh / 2, x2 = xx + ww - hh / 2;
        double percent = Tools.min(Tools.max((mouseX() * screen.pxWidth - x1) / (x2 - x1), 0), 1);

        value = minValue + (maxValue - minValue) * percent;
        knob.x = (1 - knob.width) * percent;
    }

    public void setValue(double value)
    {
        double percent = Tools.min(Tools.max((value - minValue) / (maxValue - minValue), 0), 1);

        this.value = minValue + (maxValue - minValue) * percent;
        knob.x = (width - height) * percent;
    }

    public double getValue()
    {
        return value;
    }


    public void addDragActions(Runnable... actions)
    {
        dragActions.addAll(Arrays.asList(actions));
    }
}
