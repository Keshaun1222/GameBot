package com.keshaun.gamesbot;

import javax.swing.*;

public class GUI {
	private JFrame frame;
    public GUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
        frame.setTitle("Bot GUI");
        frame.setSize(30,20);
        frame.setResizable(false);
    }
    
    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
