package com.fantasticsource.faeruncharacters;

import com.fantasticsource.faeruncharacters.config.FaerunCharactersConfig;
import com.fantasticsource.faeruncharacters.nbt.CharacterTags;
import com.fantasticsource.mctools.MCTools;
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
        WRAPPER.registerMessage(PersonalPortalGUIPacketHandler.class, CharacterCustomizationGUIPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(SetCCPacketHandler.class, SetCCPacket.class, discriminator++, Side.SERVER);
    }


    public static class CharacterCustomizationGUIPacket implements IMessage
    {
        public boolean isPremium;
        public LinkedHashMap<String, CRace> races;
        public LinkedHashMap<String, CRace> racesPremium;
        public HashSet<String> bareArms, faceAccessories, headAccessories;
        public NBTTagCompound ccCompound;

        public CharacterCustomizationGUIPacket()
        {
            //Required
        }

        public CharacterCustomizationGUIPacket(EntityPlayerMP player)
        {
            isPremium = MCTools.isWhitelisted(player);
            ccCompound = CharacterTags.getCC(player);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(isPremium);

            ByteBufUtils.writeUTF8String(buf, ccCompound.toString());

            buf.writeInt(FaerunCharactersConfig.server.bareArmSkinSet.size());
            for (String bareArmSkin : FaerunCharactersConfig.server.bareArmSkinSet) ByteBufUtils.writeUTF8String(buf, bareArmSkin);

            buf.writeInt(FaerunCharactersConfig.server.faceAccessorySet.size());
            for (String bareArmSkin : FaerunCharactersConfig.server.faceAccessorySet) ByteBufUtils.writeUTF8String(buf, bareArmSkin);

            buf.writeInt(FaerunCharactersConfig.server.headAccessorySet.size());
            for (String bareArmSkin : FaerunCharactersConfig.server.headAccessorySet) ByteBufUtils.writeUTF8String(buf, bareArmSkin);

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

    public static class PersonalPortalGUIPacketHandler implements IMessageHandler<CharacterCustomizationGUIPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(CharacterCustomizationGUIPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> new CharacterCustomizationGUI(packet));
            return null;
        }
    }


    public static class SetCCPacket implements IMessage
    {
        public NBTTagCompound ccCompound;

        public SetCCPacket()
        {
            //Required
        }

        public SetCCPacket(NBTTagCompound ccCompound)
        {
            this.ccCompound = ccCompound;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, ccCompound.toString());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            try
            {
                ccCompound = JsonToNBT.getTagFromJson(ByteBufUtils.readUTF8String(buf));
            }
            catch (NBTException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class SetCCPacketHandler implements IMessageHandler<SetCCPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SetCCPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> CharacterTags.setCC(ctx.getServerHandler().player, packet.ccCompound));
            return null;
        }
    }
}
