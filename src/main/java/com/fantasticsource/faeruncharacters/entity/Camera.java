package com.fantasticsource.faeruncharacters.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Camera extends ClientEntity
{
    protected static Camera camera = null;

    static
    {
        MinecraftForge.EVENT_BUS.register(Camera.class);
    }

    protected boolean active = false;
    protected int oMode;
    protected Camera(World worldIn)
    {
        super(worldIn);
        forceSpawn = true;
    }

    public static Camera getCamera()
    {
        if (camera == null) camera = new Camera(null);
        return camera;
    }

    @SubscribeEvent
    public static void renderPlayerPre(RenderPlayerEvent.Pre event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (getCamera().active && event.getEntityPlayer() == mc.player)
        {
            mc.getRenderManager().renderViewEntity = mc.player;
        }
    }

    @SubscribeEvent
    public static void renderPlayerPost(RenderPlayerEvent.Post event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (getCamera().active && event.getEntityPlayer() == mc.player)
        {
            mc.getRenderManager().renderViewEntity = getCamera();
        }
    }

    public void activate(World world, double x, double y, double z, float yaw, float pitch)
    {
        if (active)
        {
            if (world != this.world) deactivate();
            else
            {
                setPositionAndRotation(x, y, z, yaw, pitch);
                return;
            }
        }


        //Set state
        active = true;


        //Entity
        this.world = world;
        this.dimension = world.provider.getDimension();
        this.posX = x;
        this.prevPosX = x;
        this.posY = y;
        this.prevPosY = y;
        this.posZ = z;
        this.prevPosZ = z;
        this.rotationYaw = yaw;
        this.prevRotationYaw = yaw;
        this.rotationPitch = pitch;
        this.prevRotationPitch = pitch;
        world.spawnEntity(this);


        //Mode
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gs = mc.gameSettings;
        oMode = gs.thirdPersonView;
        gs.thirdPersonView = 1;


        //Set camera
        Minecraft.getMinecraft().setRenderViewEntity(camera);
    }

    public void deactivate()
    {
        if (active)
        {
            //Set state
            active = false;


            //Entity
            world.removeEntity(this);
            world = null;


            //Mode
            Minecraft mc = Minecraft.getMinecraft();
            GameSettings gs = mc.gameSettings;
            gs.thirdPersonView = oMode;


            //Set camera
            mc.setRenderViewEntity(mc.player);
        }
    }

    @Override
    public void onUpdate()
    {
        //Mode
        if (active) Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;

        super.onUpdate();
    }

    public void setPositionAndRotation(Vec3d position, float yaw, float pitch)
    {
        setPosition(position);
        setRotation(yaw, pitch);
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    public void setPosition(Vec3d vec)
    {
        setPosition(vec.x, vec.y, vec.z);
    }

    @Override
    public void setPosition(double x, double y, double z)
    {
        posX = x;
        prevPosX = x;
        posY = y;
        prevPosY = y;
        posZ = z;
        prevPosZ = z;
    }

    @Override
    public void setRotation(float yaw, float pitch)
    {
        rotationYaw = yaw;
        prevRotationYaw = yaw;
        rotationPitch = pitch;
        prevRotationPitch = pitch;
    }

    @Override
    public float getEyeHeight()
    {
        return 0;
    }
}
