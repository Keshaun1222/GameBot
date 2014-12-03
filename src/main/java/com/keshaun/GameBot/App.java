package com.keshaun.GameBot;

import com.keshaun.GameBot.commands.BlackjackCommands;
import com.keshaun.GameBot.commands.MafiaCommands;
import com.keshaun.GameBot.commands.ManagementCommands;
import com.keshaun.GameBot.commands.PokerCommands;
import com.keshaun.GameBot.events.EventHandler;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class App {
    //User levels for the Bot
    public final static int OWNER = 100;
    public final static int ADMIN = 50;
    public final static int MOD = 25;
    public final static int BANNED = 0;

    //Game status variables
    public static boolean gameOn = false;
    public static boolean gameQueue = false;
    public static boolean gameInProgress = false;
    public static int gameType = 0;

    //Variables for config
    public static Logger logger = LoggerFactory.getLogger(App.class);
    public static Map<String, Object> conf = null;
    public static Yaml yaml = new Yaml();
    public static File configurationFile = new File("config/config.yml");

    //GUI variable
    public static boolean useGUI = true;

    //Function for setting up config file
    @SuppressWarnings("unchecked")
    public static void setupFolders() {
        File f = new File("config");
        f.mkdir();
        try {
            if (!configurationFile.exists()) {
                configurationFile.createNewFile();
                Scanner scanner = new Scanner(App.class.getResourceAsStream("./defaultConfig.yml"));
                FileWriter fileWriter = new FileWriter(configurationFile);
                while (scanner.hasNextLine()) {
                    fileWriter.write(scanner.nextLine() + '\n');
                }
                fileWriter.close();
                scanner.close();
                logger.info("Finished writing default config.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conf = (Map<String, Object>) yaml.load(new FileInputStream(configurationFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            //Checks if one of the args is "nogui"
            if (args[i].contains("nogui")) {
                useGUI = false;
            }
        }

        //If conditions met, start GUI for easy shutdown.
        if (!GraphicsEnvironment.isHeadless() && useGUI)
            new GUI();

        setupFolders();
        new App();
    }

    //Bot's IRC configurations
    Configuration<PircBotX> botConf = new Configuration.Builder<PircBotX>()
            .setName((String) conf.get("name"))
            .setLogin((String) conf.get("login"))
            .setRealName((String) conf.get("realname"))
            .setAutoNickChange(true)
            .setServer((String) conf.get("hostname"), (Integer) conf.get("port"))
            .addAutoJoinChannel("#" + (String) conf.get("autojoinchannel"), (String) conf.get("channelkey"))
            .addListener(new EventHandler())
            .addListener(new BlackjackCommands())
            .addListener(new MafiaCommands())
            .addListener(new PokerCommands())
            .addListener(new ManagementCommands())
            .setNickservPassword((String) conf.get("nickserv"))
            .buildConfiguration();

    //Bot class
    public App() {
        PircBotX bot = new PircBotX(botConf);

        try {
            bot.startBot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }
}