package com.keshaun.gamesbot.objects;

import org.pircbotx.Colors;

public class Card {
	private String value;
	private String suit;
	
	public Card(String value, String suit) {
		this.value = value;
		this.suit = suit;
	}
	
	@Override
	public String toString() {
        String color;

        if ("D".equalsIgnoreCase(suit)) {
            color = Colors.RED;
        } else if ("C".equalsIgnoreCase(suit)) {
            color = Colors.BLACK;
        } else if ("H".equalsIgnoreCase(suit)) {
            color = Colors.BROWN;
        } else {
            color = Colors.BLUE;
        }

        return color + "[" + value + "]";
	}
}
