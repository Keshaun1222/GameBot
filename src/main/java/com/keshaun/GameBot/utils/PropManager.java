package com.keshaun.GameBot.utils;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class PropManager {
	private static final Properties PROP = new Properties();
	private static final String CONFIGFILE = "resources/config.properties";
	
	private static final Logger LOGGER = Logger.getLogger("DBLogger");
	
	public static final String HOSTNAME;
	public static final int PORT;
	public static final String NAME;
	public static final String LOGIN;
	public static final String REALNAME;
	public static final String AUTOJOIN;
	public static final String CHANNELKEY;
	public static final String NICKSERV;
	public static final String DBHOST;
	public static final String DB;
	public static final String DBUSER;
	public static final String DBPASS;
	
	static {
		InputStream input = PropManager.class.getClassLoader().getResourceAsStream(CONFIGFILE);
		try {
			if (input != null) {
				PROP.load(input);
			} else {
				throw new FileNotFoundException("Properties file not found...");
			}	
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Input/Output Error", e);
		}
		
		HOSTNAME = PROP.getProperty("hostname");
		PORT = Integer.parseInt(PROP.getProperty("port"));
		NAME = PROP.getProperty("name");
		LOGIN = PROP.getProperty("login");
		REALNAME = PROP.getProperty("realname");
		AUTOJOIN = PROP.getProperty("autojoinchannel");
		CHANNELKEY = PROP.getProperty("channelkey");
		NICKSERV = PROP.getProperty("dwayne12");
		DBHOST = PROP.getProperty("dbhost");
		DB = PROP.getProperty("db");
		DBUSER = PROP.getProperty("dbuser");
		DBPASS = PROP.getProperty("dbpass");
	}
}
