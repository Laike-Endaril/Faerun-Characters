package com.fantasticsource.faeruncharacters.gui;

import com.fantasticsource.faeruncharacters.entity.Camera;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;

public class GUICCCameraController extends GUIElement
{
    protected double lastX, lastY;
    protected CharacterCustomizationGUI gui;

    public GUICCCameraController(CharacterCustomizationGUI screen, double x, double y, double width, double height)
    {
        super(screen, x, y, width, height);
        gui = screen;
    }

    @Override
    public boolean mousePressed(int button)
    {
        boolean done = false;
        for (GUIElement child : children) done |= child.mousePressed(button);

        if (!done && button == 0)
        {
            if (isMouseWithin()) setActive(true);

            lastX = mouseX();
            lastY = mouseY();
        }

        return true;
    }

    @Override
    public void mouseDrag(int button)
    {
        if (active && button == 0)
        {
            double mX = mouseX(), mY = mouseY();

            Camera camera = Camera.getCamera();
            camera.setRotation((float) Tools.posMod(camera.rotationYaw + (mX - lastX) * 550, 360), (float) Tools.min(Tools.max(camera.rotationPitch + (mY - lastY) * 250, -89), 89));
            lastX = mX;
            lastY = mY;
        }

        super.mouseDrag(button);
    }
}
