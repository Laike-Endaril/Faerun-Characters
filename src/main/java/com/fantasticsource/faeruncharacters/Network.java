package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.gui.CharacterCustomizationGUI;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(CharacterCustomizationGUIPacketHandler.class, CharacterCustomizationGUIPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(SetBodyTypePacketHandler.class, SetBodyTypePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SetChestTypePacketHandler.class, SetChestTypePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SetCCSkinPacketHandler.class, SetCCSkinPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SetCCColorPacketHandler.class, SetCCColorPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SetCCIntPacketHandler.class, SetCCIntPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SetCCDoublePacketHandler.class, SetCCDoublePacket.class, discriminator++, Side.SERVER);
    }


    public static class CharacterCustomizationGUIPacket implements IMessage
    {
        public boolean isPremium;
        public String bodyType, chest;
        public NBTTagCompound ccCompound;

        public LinkedHashMap<String, CRace> races;
        public LinkedHashMap<String, CRace> racesPremium;
        public HashSet<String> bareArms, markings, faceAccessories, headAccessories;

        public CharacterCustomizationGUIPacket()
        {
            //Required
        }

        public CharacterCustomizationGUIPacket(EntityPlayerMP player)
        {
            isPremium = MCTools.isWhitelisted(player);

            bodyType = RenderModes.getRenderMode(player, "Body");
            if (bodyType != null) bodyType = bodyType.equals("M") ? "Masculine" : "Feminine";

            chest = RenderModes.getRenderMode(player, "Chest");
            if (chest == null) chest = "Flat";

            ccCompound = CharacterTags.getCC(player);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(isPremium);

            ByteBufUtils.writeUTF8String(buf, bodyType);
            ByteBufUtils.writeUTF8String(buf, chest);

            ByteBufUtils.writeUTF8String(buf, ccCompound.toString());

            buf.writeInt(FaerunCharactersConfig.server.bareArmSkinSet.size());
            for (String skinName : FaerunCharactersConfig.server.bareArmSkinSet) ByteBufUtils.writeUTF8String(buf, skinName);

            buf.writeInt(FaerunCharactersConfig.server.markingsSet.size());
            for (String skinName : FaerunCharactersConfig.server.markingsSet) ByteBufUtils.writeUTF8String(buf, skinName);

            buf.writeInt(FaerunCharactersConfig.server.faceAccessorySet.size());
            for (String skinName : FaerunCharactersConfig.server.faceAccessorySet) ByteBufUtils.writeUTF8String(buf, skinName);

            buf.writeInt(FaerunCharactersConfig.server.headAccessorySet.size());
            for (String skinName : FaerunCharactersConfig.server.headAccessorySet) ByteBufUtils.writeUTF8String(buf, skinName);

            buf.writeInt(CRace.RACES.size());
            for (Map.Entry<String, CRace> entry : CRace.RACES.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());
                entry.getValue().write(buf);
            }

            buf.writeInt(CRace.RACES_PREMIUM.size());
            for (Map.Entry<String, CRace> entry : CRace.RACES_PREMIUM.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());
                entry.getValue().write(buf);
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            isPremium = buf.readBoolean();

            bodyType = ByteBufUtils.readUTF8String(buf);
            chest = ByteBufUtils.readUTF8String(buf);

            try
            {
                ccCompound = JsonToNBT.getTagFromJson(ByteBufUtils.readUTF8String(buf));
            }
            catch (NBTException e)
            {
                e.printStackTrace();
                return;
            }

            bareArms = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--) bareArms.add(ByteBufUtils.readUTF8String(buf));

            markings = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--) markings.add(ByteBufUtils.readUTF8String(buf));

            faceAccessories = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--) faceAccessories.add(ByteBufUtils.readUTF8String(buf));

            headAccessories = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--) headAccessories.add(ByteBufUtils.readUTF8String(buf));

            int size = buf.readInt();
            races = new LinkedHashMap<>(size);
            for (int i = 0; i < size; i++)
            {
                races.put(ByteBufUtils.readUTF8String(buf), new CRace().read(buf));
            }

            size = buf.readInt();
            racesPremium = new LinkedHashMap<>(size);
            for (int i = 0; i < size; i++)
            {
                racesPremium.put(ByteBufUtils.readUTF8String(buf), new CRace().read(buf));
            }
        }
    }

    public static class CharacterCustomizationGUIPacketHandler implements IMessageHandler<CharacterCustomizationGUIPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(CharacterCustomizationGUIPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> new CharacterCustomizationGUI(packet));
            return null;
        }
    }


    public static class SetBodyTypePacket implements IMessage
    {
        public String type;

        public SetBodyTypePacket()
        {
            //Required
        }

        public SetBodyTypePacket(String type)
        {
            this.type = type;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, type);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            type = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class SetBodyTypePacketHandler implements IMessageHandler<SetBodyTypePacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetBodyTypePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                if (ctx.getServerHandler().player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
                {
                    RenderModes.setRenderMode(ctx.getServerHandler().player, "Body", packet.type);
                }
            });
            return null;
        }
    }


    public static class SetChestTypePacket implements IMessage
    {
        public String type;

        public SetChestTypePacket()
        {
            //Required
        }

        public SetChestTypePacket(String type)
        {
            this.type = type;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, type);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            type = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class SetChestTypePacketHandler implements IMessageHandler<SetChestTypePacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetChestTypePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                if (ctx.getServerHandler().player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
                {
                    RenderModes.setRenderMode(ctx.getServerHandler().player, "Chest", packet.type);
                }
            });
            return null;
        }
    }


    public static class SetCCSkinPacket implements IMessage
    {
        String key, value;

        public SetCCSkinPacket()
        {
            //Required
        }

        public SetCCSkinPacket(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, key);
            ByteBufUtils.writeUTF8String(buf, value);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            key = ByteBufUtils.readUTF8String(buf);
            value = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class SetCCSkinPacketHandler implements IMessageHandler<SetCCSkinPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetCCSkinPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                if (ctx.getServerHandler().player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
                {
                    CharacterTags.setCCSkin(ctx.getServerHandler().player, packet.key, packet.value);
                }
            });
            return null;
        }
    }


    public static class SetCCColorPacket implements IMessage
    {
        String key;
        int value;

        public SetCCColorPacket()
        {
            //Required
        }

        public SetCCColorPacket(String key, int value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, key);
            buf.writeInt(value);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            key = ByteBufUtils.readUTF8String(buf);
            value = buf.readInt();
        }
    }

    public static class SetCCColorPacketHandler implements IMessageHandler<SetCCColorPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetCCColorPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                if (ctx.getServerHandler().player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
                {
                    CharacterTags.setCCColor(ctx.getServerHandler().player, packet.key, new Color(packet.value));
                }
            });
            return null;
        }
    }


    public static class SetCCIntPacket implements IMessage
    {
        String key;
        int value;

        public SetCCIntPacket()
        {
            //Required
        }

        public SetCCIntPacket(String key, int value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, key);
            buf.writeInt(value);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            key = ByteBufUtils.readUTF8String(buf);
            value = buf.readInt();
        }
    }

    public static class SetCCIntPacketHandler implements IMessageHandler<SetCCIntPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetCCIntPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                if (ctx.getServerHandler().player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
                {
                    CharacterTags.getCC(ctx.getServerHandler().player).setInteger(packet.key, packet.value);
                }
            });
            return null;
        }
    }


    public static class SetCCDoublePacket implements IMessage
    {
        String key;
        double value;

        public SetCCDoublePacket()
        {
            //Required
        }

        public SetCCDoublePacket(String key, double value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, key);
            buf.writeDouble(value);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            key = ByteBufUtils.readUTF8String(buf);
            value = buf.readDouble();
        }
    }

    public static class SetCCDoublePacketHandler implements IMessageHandler<SetCCDoublePacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetCCDoublePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                if (ctx.getServerHandler().player.world.provider.getDimensionType() == CharacterCustomization.DIMTYPE_CHARACTER_CREATION)
                {
                    CharacterTags.getCC(ctx.getServerHandler().player).setDouble(packet.key, packet.value);
                }
            });
            return null;
        }
    }
}
