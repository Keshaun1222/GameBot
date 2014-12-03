package com.keshaun.GameBot;

import javax.swing.*;

public class GUI {
    public GUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
        frame.setTitle("Bot GUI");
        frame.setSize(30,20);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
