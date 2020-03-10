package com.fantasticsource.faeruncharacters;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.tools.datastructures.SortableTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.faeruncharacters.FaerunCharacters.MODID;

public class Network
{
//    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
//    private static final LinkedHashMap<EntityPlayerMP, World> personalPortalWorlds = new LinkedHashMap<>();
//    private static final LinkedHashMap<EntityPlayerMP, BlockPos> personalPortalPositions = new LinkedHashMap<>();
//    private static int discriminator = 0;
//
//    public static void init()
//    {
//        WRAPPER.registerMessage(PersonalPortalGUIPacketHandler.class, PersonalPortalGUIPacket.class, discriminator++, Side.CLIENT);
//        WRAPPER.registerMessage(PersonalPortalPacketHandler.class, PersonalPortalPacket.class, discriminator++, Side.SERVER);
//    }
//
//
//    public static class PersonalPortalGUIPacket implements IMessage
//    {
//        EntityPlayerMP player;
//        ArrayList<String> namesIn;
//        public String[] namesOut;
//        public boolean isInInstance, isInOwnedInstance;
//
//        public PersonalPortalGUIPacket()
//        {
//            //Required
//        }
//
//        public PersonalPortalGUIPacket(EntityPlayerMP player)
//        {
//            this.player = player;
//            personalPortalWorlds.put(player, player.world);
//            personalPortalPositions.put(player, player.getPosition());
//        }
//
//        @Override
//        public void toBytes(ByteBuf buf)
//        {
//            InstanceWorldInfo info = InstanceHandler.get(player.dimension);
//
//            buf.writeBoolean(info != null);
//            buf.writeBoolean(info != null && player.getPersistentID().equals(info.getOwner()));
//
//            String ownername = info == null ? null : PlayerData.getName(info.getOwner());
//            namesIn = new ArrayList<>();
//            VisitablePlayersData data = InstanceHandler.visitablePlayersData.get(player.getPersistentID());
//            if (data != null)
//            {
//                Object[] nameTables = data.visitablePlayers.getColumn(1);
//                for (Object nameTable : nameTables)
//                {
//                    for (Object name : ((SortableTable) nameTable).getColumn(0))
//                    {
//                        if (!name.equals(ownername)) namesIn.add((String) name);
//                    }
//                }
//            }
//
//            buf.writeInt(namesIn.size());
//            for (String name : namesIn) ByteBufUtils.writeUTF8String(buf, name);
//        }
//
//        @Override
//        public void fromBytes(ByteBuf buf)
//        {
//            isInInstance = buf.readBoolean();
//            isInOwnedInstance = buf.readBoolean();
//
//            int size = buf.readInt();
//            namesOut = new String[size];
//            for (int i = 0; i < size; i++)
//            {
//                namesOut[i] = ByteBufUtils.readUTF8String(buf);
//            }
//        }
//    }
//
//    public static class PersonalPortalGUIPacketHandler implements IMessageHandler<PersonalPortalGUIPacket, IMessage>
//    {
//        @Override
//        @SideOnly(Side.CLIENT)
//        public IMessage onMessage(PersonalPortalGUIPacket packet, MessageContext ctx)
//        {
//            Minecraft.getMinecraft().addScheduledTask(() -> CharacterCreationGUI.show(packet));
//            return null;
//        }
//    }
//
//
//    public static class PersonalPortalPacket implements IMessage
//    {
//        String selection;
//
//        public PersonalPortalPacket()
//        {
//            //Required
//        }
//
//        public PersonalPortalPacket(String selection)
//        {
//            this.selection = selection;
//        }
//
//        @Override
//        public void toBytes(ByteBuf buf)
//        {
//            ByteBufUtils.writeUTF8String(buf, selection);
//        }
//
//        @Override
//        public void fromBytes(ByteBuf buf)
//        {
//            selection = ByteBufUtils.readUTF8String(buf);
//        }
//    }
//
//    public static class PersonalPortalPacketHandler implements IMessageHandler<PersonalPortalPacket, IMessage>
//    {
//        @Override
//        public IMessage onMessage(PersonalPortalPacket message, MessageContext ctx)
//        {
//            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
//            {
//                EntityPlayerMP player = ctx.getServerHandler().player;
//                if (player.world != personalPortalWorlds.get(player)) return;
//                if (personalPortalPositions.get(player).distanceSq(player.getPosition()) > 9) return;
//
//                String s = message.selection;
//                if (s == null) return;
//
//                switch (s)
//                {
//                    case "Leave Instance":
//                        Teleport.escape(player);
//                        return;
//
//                    case "Go Home":
//                        Teleport.joinPossiblyCreating(player);
//                        return;
//
//                    default:
//                        VisitablePlayersData data = InstanceHandler.visitablePlayersData.get(player.getPersistentID());
//                        if (data == null) return;
//
//                        SortableTable nameTable = (SortableTable) data.visitablePlayers.get(0, s.charAt(0), 1);
//                        if (nameTable == null || !nameTable.contains(s, 0)) return;
//
//                        Teleport.joinPossiblyCreating(player, s);
//                }
//            });
//            return null;
//        }
//    }
}
