package net.keshaun.gamesbot.objects;

import org.pircbotx.Colors;

public class Card {
	private String value;
	private String suit;
	private int[] points = new int[2];
	
	public Card(String value, String suit) {
		this.value = value;
		this.suit = suit;
		this.points[1] = 0;
		if (value.matches("[2-9]?")) {
			this.points[0] = Integer.parseInt(value);
		} else if ("A".equals(value)) {
			this.points[0] = 1;
			this.points[1] = 11;
		} else {
			this.points[0] = 10;
		}
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
	
	public int[] getPoints() {
		return points;
	}
	
	public String getPointsString() {
		String returns = "";
		returns += points[0];
		if (points[1] != 0) {
			returns += " or " + points[1];
		}
		return returns;
	}
}
