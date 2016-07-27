package com.corwinjv.mobtotems.blocks.tiles;

import baubles.api.BaublesApi;
import com.corwinjv.mobtotems.interfaces.IChargeable;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by CorwinJV on 7/24/2016.
 */
public class IncenseKindlingBoxTileEntity extends ModTileEntity
{
    private static final String CUR_TTL = "CUR_TTL";

    private static final long UPDATE_TICKS = 20;
    private static final long PARTICLE_UPDATE_TICKS = 40;
    public static final double TMP_MANA_GAIN_DIST = 8;
    public static final int CHARGE_GAIN_PER_TICK = 2;
    private static final long TTL = 200;

    private long curTTL = 0;

    public IncenseKindlingBoxTileEntity()
    {
        super();

    }

    public void setCurTTL(long ttl)
    {
        curTTL = ttl;
        markForUpdate();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setLong(CUR_TTL, curTTL);
        return ret;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        long time = 0;
        if(compound.hasKey(CUR_TTL))
        {
            time = compound.getLong(CUR_TTL);
        }
        curTTL = time;
    }

    @Override
    public void update()
    {
        if(!getWorld().isRemote)
        {
            performTTLUpdate();

            long worldTime = getWorld().getWorldTime();
            if(worldTime % UPDATE_TICKS == 0)
            {
                performChargeAura();
            }
        }
        else
        {
            long worldTime = getWorld().getWorldTime();
            if(worldTime % PARTICLE_UPDATE_TICKS == 0)
            {
                spawnParticleEffects();
            }
        }
    }

    private void performTTLUpdate()
    {
        curTTL++;
        if(curTTL > TTL)
        {
            getWorld().destroyBlock(pos, false);
        }
    }

    private void performChargeAura()
    {
        final BlockPos targetPos = getPos();
        Predicate<EntityPlayer> playerWithinRangePredicate = new Predicate<EntityPlayer>()
        {
            @Override
            public boolean apply(@Nullable EntityPlayer input)
            {
                return input != null && input.getPosition().getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < TMP_MANA_GAIN_DIST;
            }
        };
        List<EntityPlayer> playersWithinRange = getWorld().getEntities(EntityPlayer.class, playerWithinRangePredicate);

        for(EntityPlayer player : playersWithinRange)
        {
            IInventory inventory = BaublesApi.getBaubles(player);
            for(int i = 0; i < inventory.getSizeInventory(); i++)
            {
                final ItemStack baubleStack = inventory.getStackInSlot(i);
                if(baubleStack != null
                        && baubleStack.getItem() instanceof IChargeable)
                {
                    ((IChargeable) baubleStack.getItem()).incrementChargeLevel(baubleStack, CHARGE_GAIN_PER_TICK);
                }
            }
        }
    }

    private void spawnParticleEffects()
    {
        double startX = pos.getX() - TMP_MANA_GAIN_DIST;
        double startY = pos.getY() - TMP_MANA_GAIN_DIST;
        double startZ = pos.getZ() - TMP_MANA_GAIN_DIST;

        for(double x = startX; x < pos.getX() + TMP_MANA_GAIN_DIST; x++)
        {
            for(double y = startY; y < pos.getY() + TMP_MANA_GAIN_DIST; y++)
            {
                for(double z = startZ; z < pos.getZ() + TMP_MANA_GAIN_DIST; z++)
                {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    Block block = getWorld().getBlockState(blockPos).getBlock();
                    Block blockAbove = getWorld().getBlockState(new BlockPos(x, y+1, z)).getBlock();

                    if(pos.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < TMP_MANA_GAIN_DIST
                            && block.isBlockSolid(getWorld(), blockPos, EnumFacing.UP)
                            && blockAbove instanceof BlockAir)
                    {
                        Random rand = getWorld().rand;
                        float width = 0.75f;
                        float height = 0.75f;

                        double motionX = rand.nextGaussian() * 0.02D;
                        double motionY = rand.nextGaussian() * 0.02D;
                        double motionZ = rand.nextGaussian() * 0.02D;
                        worldObj.spawnParticle(
                                EnumParticleTypes.CLOUD,
                                x + rand.nextFloat() * width * 2.0F - width,
                                y + 1.0D + rand.nextFloat() * height,
                                z + rand.nextFloat() * width * 2.0F - width,
                                motionX,
                                motionY,
                                motionZ);

                        width = 1.0f;
                        height = 1.0f;

                        motionX = rand.nextGaussian() * 0.02D;
                        motionY = rand.nextGaussian() * 0.02D;
                        motionZ = rand.nextGaussian() * 0.02D;
                        worldObj.spawnParticle(
                                EnumParticleTypes.VILLAGER_HAPPY,
                                x + rand.nextFloat() * width * 2.0F - width,
                                y + 0.5D + rand.nextFloat() * height,
                                z + rand.nextFloat() * width * 2.0F - width,
                                motionX,
                                motionY,
                                motionZ);
                    }
                }
            }
        }
    }
}
