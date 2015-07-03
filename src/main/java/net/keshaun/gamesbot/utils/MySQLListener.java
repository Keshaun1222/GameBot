package net.keshaun.gamesbot.utils;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MySQLListener extends ListenerAdapter<PircBotX> {
    protected Connection c = null;

    protected static final Logger LOGGER = Logger.getLogger(MySQLListener.class.getName());
    
    public MySQLListener() {
        setupDB();
        LOGGER.setLevel(Level.ALL);

        ConsoleHandler handler = new ConsoleHandler();

        LOGGER.addHandler(handler);
    }

    protected abstract void setupDB();

    public void openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://" + PropManager.DBHOST + "/" + PropManager.DB, PropManager.DBUSER, PropManager.DBPASS);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, ErrorMessages.CLASS_NOT_FOUND, e);
        }
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, ErrorMessages.SQL, e);
        }
    }
}
