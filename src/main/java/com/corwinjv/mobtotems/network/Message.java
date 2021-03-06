package com.corwinjv.mobtotems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by CorwinJV on 2/1/2016.
 */
public class Message<REQ extends IMessage> implements IMessage, IMessageHandler<REQ, REQ>
{
    @Override
    public REQ onMessage(REQ aMessage, MessageContext aCtx) {
        final REQ message = aMessage;
        final MessageContext ctx = aCtx;

        if(ctx.side == Side.SERVER)
        {
            ((IThreadListener) ctx.getServerHandler().playerEntity.world).addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    handleServer(message, ctx.getServerHandler().playerEntity);
                }
            });
        }
        else if(ctx.side == Side.CLIENT)
        {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    handleClient(message, Minecraft.getMinecraft().player);
                }
            });
        }
        return null;
    }

    protected void handleClient(REQ Message, EntityPlayer player)
    {

    }

    protected void handleServer(REQ Message, EntityPlayerMP player)
    {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
