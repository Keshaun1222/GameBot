package com.keshaun.GameBot.utils;

import com.keshaun.GameBot.App;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MySQLListener extends ListenerAdapter<PircBotX> {
    protected Connection c = null;

    private static final Logger LOGGER = (Logger) Logger.getLogger("BotLogger");
    
    public MySQLListener() {
        setupDB();
    }

    public abstract void setupDB();

    public void openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://" + PropManager.DBHOST + "/" + PropManager.DB, PropManager.DBUSER, PropManager.DBPASS);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Class not Found Error", e);
        }
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, "SQL Error", e);
        }
    }
}
