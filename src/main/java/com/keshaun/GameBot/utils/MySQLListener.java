package com.keshaun.GameBot.utils;

import com.keshaun.GameBot.App;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class MySQLListener extends ListenerAdapter<PircBotX> {
    protected Connection c = null;

    public MySQLListener() {
        setupDB();
    }

    public abstract void setupDB();

    public void openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://" + App.conf.get("dbhost") + "/" + App.conf.get("db") + "?" + "user=" + App.conf.get("dbuser") + "&password=" + App.conf.get("dbpass"));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
