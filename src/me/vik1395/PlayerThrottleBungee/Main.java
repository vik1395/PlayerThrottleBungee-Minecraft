package me.vik1395.PlayerThrottleBungee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/*

Author: Vik1395
Project: PlayerThrottleBungee

Copyright 2014

Licensed under Creative CommonsAttribution-ShareAlike 4.0 International Public License (the "License");
You may not use this file except in compliance with the License.

You may obtain a copy of the License at http://creativecommons.org/licenses/by-sa/4.0/legalcode

You may find an abridged version of the License at http://creativecommons.org/licenses/by-sa/4.0/
 */

public class Main extends Plugin implements Listener
{
	private HashMap<String, String> throttle1 = new HashMap<String, String>();
	private String msg = "";
	private int maxconn = 1;
	public static Configuration config;
    public static ConfigurationProvider cProvider;
    public static File cFile;
	
	public void onEnable()
	{
		File cFolder = new File(this.getDataFolder(),"");
		
		if (!cFolder.exists()) 
		{
	        cFolder.mkdir();
		}
		
		cFile = new File(this.getDataFolder() + "/config.yml");
		
		if (!cFile.exists()) 
		{
	        try 
	        {
	        	String file = "Kick Message: \'[Error] too many connections from your IP\'\n"
	        			+ "# Message sent to the player when they are kicked.\n"
	        			+ "Allowed Connections: '3'\n"
	        			+ "# Number of connections allowed.\n";
	        	
	            FileWriter fw = new FileWriter(cFile);
				BufferedWriter out = new BufferedWriter(fw);
	            out.write(file);
	            out.close();
	            fw.close();
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
		}
		
		cProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
	    try 
	    {
	        config = cProvider.load(cFile);
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
		
		
		getProxy().getPluginManager().registerListener(this, this);
		getLogger().info("PlayerThrottleBukkit has successfully started!");
		getLogger().info("Created by Vik1395");
		msg = config.getString("Kick Message");
		maxconn = Integer.parseInt(config.getString(("Allowed Connections")));
	}
	
	@EventHandler
	public void onPlayerPreLogin(PreLoginEvent ple)
	{
		String ipn = "" + ple.getConnection().getAddress().getAddress();
		String ip = ipn.substring(1,ipn.length());
		System.out.println(ip);
		boolean first = true;
		
		for(Map.Entry<String, String> entry1 : throttle1.entrySet()) 
		{
			if(entry1.getKey().equals(ip))
			{
				first = false;
				int s = Integer.parseInt(throttle1.get(ip));
				
				if(s>=maxconn)
				{
					ple.setCancelReason(msg);;
				}
				
				else
				{
					s = s+1;
					String num = "" + s;
					throttle1.remove(entry1.getKey());
					throttle1.put(ip, num);
				}
			}
		}
		if(first==true)
		{
			String num = "1";
			throttle1.put(ip, num);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onLeave(PlayerDisconnectEvent pqe)
	{
		String ipn = "" + pqe.getPlayer().getAddress().getAddress();
		String ip = ipn.substring(1,ipn.length());
		
		for (Iterator<Map.Entry<String, String>> it = throttle1.entrySet().iterator();it.hasNext();) 
        {
			Map.Entry<String, String> x = it.next();
            if(x.getKey().equals(ip))
            {
                int s = Integer.parseInt(x.getValue());
                
                if(s==1)
				{
					it.remove();
				}
				
				else
				{
					s = s-1;
					String num = "" + s;
					it.remove();
					throttle1.put(ip, num);
				}
            }
        }
		
		/*for(HashMap.Entry<String, String> entry1 : throttle1.entrySet()) 
		{
			if(entry1.getKey().equals(ip))
			{
				int s = Integer.parseInt(throttle1.get(ip));
				
				if(s==1)
				{
					throttle1.remove(entry1.getKey());
				}
				
				else
				{
					s = s-1;
					String num = "" + s;
					throttle1.remove(entry1.getKey());
					throttle1.put(ip, num);
				}
			}
		}*/
		
	}
}
