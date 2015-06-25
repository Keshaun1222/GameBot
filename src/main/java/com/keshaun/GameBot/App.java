package com.keshaun.GameBot;

// Internal Imports
import com.keshaun.GameBot.commands.*;
import com.keshaun.GameBot.events.*;
import com.keshaun.GameBot.exceptions.*;
import com.keshaun.GameBot.utils.*;

// PircBotX Imports
import org.pircbotx.*;
import org.pircbotx.exception.*;

// Java Imports
import java.awt.*;
import java.io.*;
import java.util.logging.*;

public class App {
    // User Levels for the Bot
    public final static int OWNER = 100;
    public final static int ADMIN = 50;
    public final static int MOD = 25;
    public final static int BANNED = 0;

    // Game Status Variables
    protected boolean gameOn = false;
    protected boolean gameQueue = false;
    protected boolean gameInProgress = false;
    protected int gameType = 0;

    // Logger Variable
    public static final Logger LOGGER = Logger.getLogger("BotLogger");

    // Param-Based Variables
    public boolean useGUI = true;
    public boolean gameBot = true;

    public PircBotX bot;

    //Bot's IRC Configuration (Games)
    Configuration<PircBotX> gameBotConfig = new Configuration.Builder<PircBotX>()
            .setName(PropManager.NAME)
            .setLogin(PropManager.LOGIN)
            .setRealName(PropManager.REALNAME)
            .setAutoNickChange(true)
            .setServer(PropManager.HOSTNAME, PropManager.PORT)
            .addAutoJoinChannel("#" + PropManager.AUTOJOIN, PropManager.CHANNELKEY)
            .addListener(new EventHandler())
            .addListener(new BlackjackCommands())
            .addListener(new MafiaCommands())
            .addListener(new PokerCommands())
            .addListener(new ManagementCommands())
            .setNickservPassword(PropManager.NICKSERV)
            .buildConfiguration();
    
    //Bot's IRC Configuration (No Games)
    Configuration<PircBotX> basicBotConfig = new Configuration.Builder<PircBotX>()
            .setName(PropManager.NAME)
            .setLogin(PropManager.LOGIN)
            .setRealName(PropManager.REALNAME)
            .setAutoNickChange(true)
            .setServer(PropManager.HOSTNAME, PropManager.PORT)
            .addAutoJoinChannel("#" + PropManager.AUTOJOIN, PropManager.CHANNELKEY)
            .addListener(new EventHandler())
            .addListener(new ManagementCommands())
            .setNickservPassword(PropManager.NICKSERV)
            .buildConfiguration();
    
    // GUI Object
    protected GUI gui;
    
    // Game Status Accessors
    public boolean getGameOn() {
    	return gameOn;
    }
    
    public boolean getGameQueue() {
    	return gameQueue;
    }
    
    public boolean getGameInProgress() {
    	return gameInProgress;
    }
    
    public int getGameType() {
    	return gameType;
    }
    
    // Game Status Mutators
    public void toogleGameOn() {
    	gameOn = !gameOn;
    }
    
    public void toogleGameQueue() {
    	gameQueue = !gameQueue;
    }
    
    public void toogleGameInProgress() {
    	gameInProgress = !gameInProgress;
    }
    
    public void setGameType(int type) throws InvalidGameTypeException {
    	if (type < 0 || type > 2) {
    		throw new InvalidGameTypeException();
    	}
    	gameType = type;
    }
    
    public void setGameType() throws InvalidGameTypeException {
    	setGameType(0);
    }
    
    // Comparison Method
    @Override
    public boolean equals (Object other) {
    	boolean result = false;
    	
    	if(other instanceof App) {
    		App second = (App) other;
    		result = (this.gameBot == second.gameBot && this.useGUI == second.useGUI && this.gameOn == this.getGameOn() && this.gameQueue == this.getGameQueue() && this.gameInProgress == this.getGameInProgress() && this.gameType == this.getGameType());
    	}
    	
    	return result;
    }
    
    // Hash Code Method
    public int hashCode() {
    	int on = gameOn ? 1 : 0;
    	int queue = gameQueue ? 1 : 0;
    	int inProgress = gameInProgress ? 1 : 0;
    	int game = gameBot ? 1 : 0;
    	int use = useGUI ? 1 :0;
    	
    	return 10 * (10 * (10 * (10 * (10 * (10 + game) + use) + on) + queue) + inProgress) + gameType;
    }
    	
    
   public static void main(String[] args) {
       App app = new App();
       LOGGER.setLevel(Level.SEVERE);
       
       ConsoleHandler handler = new ConsoleHandler();
       
       LOGGER.addHandler(handler);
       
	   for (int i = 0; i < args.length; i++) {
		   if (args[i].contains("basicbot") || args[i].contentEquals("basic-bot")) {
			   app = new App();
		   }
		   
		   if (args[i].contains("nogui")) {
                app.useGUI = false;
            }
        }

        // If conditions met, start GUI for easy shutdown.
        if (!GraphicsEnvironment.isHeadless() && app.useGUI)
            app.gui = new GUI();

        app.gui.show();
        app.start();
    }

    // App Construction
    public App(String type) {
        if ("basic".equals(type)) {
        	bot = new PircBotX(basicBotConfig);
        } else {
        	bot = new PircBotX(gameBotConfig);
        }
    }
    
    public App() {
    	this("game");
    }
    
    public void start() {
    	try {
            bot.startBot();
        } catch (IOException e) {
        	LOGGER.log(Level.SEVERE, "Input/Output Error", e);
        } catch (IrcException e) {
        	LOGGER.log(Level.SEVERE, "Library");
        }
    }
}