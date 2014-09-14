package com.corwinjv.mobtotems.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

import com.corwinjv.mobtotems.Reference;

/**
 * Created by vanhc011 on 8/31/14.
 */
public class ConfigurationHandler
{
    public static Configuration configuration;

    /** Config Properties **/
    public static boolean isHardMode = false;

    public static void Init(File aConfigFile)
    {
        if(configuration == null)
        {
            configuration = new Configuration(aConfigFile);
            loadConfiguration();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.modID.equalsIgnoreCase(Reference.MOD_ID))
        {
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        isHardMode = configuration.getBoolean("HardModeOn", Configuration.CATEGORY_GENERAL, false, "Do you want to play in hard mdoe?");

        if(configuration.hasChanged())
        {
            configuration.save();
        }
    }
}