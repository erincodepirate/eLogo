package com.erinix.elogo;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class commandParser {

	public void parseCommands(StringTokenizer commandTokens, eLogoView logoView) {
		String currentToken;
//		String next;
		while (commandTokens.hasMoreTokens())
		{
			currentToken = commandTokens.nextToken();
			// since Java never implemented switch on a string, 
			// i must type in if(currentToken.equals...)
			// for each command
			if (currentToken.equals("fd")) {
				currentToken = commandTokens.nextToken();
				logoView.fd(Integer.parseInt(currentToken));
			}
			else if (currentToken.equals("rt")) {
				currentToken = commandTokens.nextToken();
				logoView.rt(Integer.parseInt(currentToken));
			}
			else if (currentToken.equals("lt")) {
				currentToken = commandTokens.nextToken();
				logoView.lt(Integer.parseInt(currentToken));
			}
			// allow both integer values for colors as well
			// as strings
			else if (currentToken.equals("color")) {
				currentToken = commandTokens.nextToken();
				// check to see if the command is followed
				// by an integer or a string, easiest done
				// with a simple pattern matching
				Pattern p = Pattern.compile("\\d*");
				Matcher m = p.matcher(currentToken);
				if (m.matches()) {
					logoView.color(
						Integer.parseInt(currentToken));
				}
				else {
					int colorNum = 1; // default to black
					if (currentToken.equals("white"))
						colorNum = 0;
					else if (currentToken.equals("black"))
						colorNum = 1;
					else if (currentToken.equals("red"))
						colorNum = 2;
					else if (currentToken.equals("green"))
						colorNum = 3;
					else if (currentToken.equals("blue"))
						colorNum = 4;
					else if (currentToken.equals("yellow"))
						colorNum = 5;
					else if (currentToken.equals("orange"))
						colorNum = 6;
					else if (currentToken.equals("pink"))
						colorNum = 7;
					else if (currentToken.equals("magenta"))
						colorNum = 8;
					else if (currentToken.equals("grey"))
						colorNum = 9;
					logoView.color(colorNum);
				}
			}
//			else if(currentToken.equals("ellipse")) {
//				currentToken = commandTokens.nextToken();
//				next = commandTokens.nextToken();
//				logoView.ellipse(Integer.parseInt(currentToken),
//					       	Integer.parseInt(next));
//			}
			else if(currentToken.equals("circle")) {
				currentToken = commandTokens.nextToken();
				logoView.circle(Integer.parseInt(currentToken));
			}
//			else if(currentToken.equals("rect")) {
//				currentToken = commandTokens.nextToken();
//				logoView.rect(Integer.parseInt(currentToken));
//			}
			else if(currentToken.equals("north")) {
				logoView.north();
			}
			else if(currentToken.equals("east")) {
				logoView.east();
			}
			else if(currentToken.equals("south")) {
				logoView.south();
			}
			else if(currentToken.equals("west")) {
				logoView.west();
			}
			else if(currentToken.equals("clear")) {
				logoView.clear();
			}
			else if(currentToken.equals("pu")) {
				logoView.penup();
			}
			else if(currentToken.equals("pd")) {
				logoView.pendown();
			}
			else if(currentToken.equals("ht")) {
				logoView.hideTurtle();
			}
			else if(currentToken.equals("st")) {
				logoView.showTurtle();
			}
			// simple variable-less for loop like the original logo's repeat command
			// syntax is repeat num [commands], should handle nested repeat loops
			else if(currentToken.equals("repeat")) {
				int times = Integer.parseInt(commandTokens.nextToken());
				int left = 0;
				int right = 0;
				boolean done = false;
				String inner = "";
				while (!done) {
					currentToken = commandTokens.nextToken();
					if (currentToken.startsWith("[")) {
						if (left == 0) {
							inner = inner + currentToken.substring(1) + " ";
						}
						else {
							inner = inner + currentToken + " ";
						}
						left++;
					}
					else if (currentToken.endsWith("]")) {
						// count the number of ]s in token, takes care of nested loops
						for (int j = 0; j < currentToken.length(); j++) {
							if (currentToken.charAt(j) == ']')
								right++;
						}
						// if we have reached the point where the command should end, 
						// cut off the end ] and add it, also signal that we are done
						if (right == left) {
							inner = inner + currentToken.substring(0, currentToken.length() - 1);
							done = true;
						}
						else {
							inner = inner + currentToken + " ";
						}
					}
					else {
						inner = inner + currentToken + " ";
					}	
				}
				// do the actual loop, recursively calling the commands inside the loop
				for (int i = 0; i < times; i++) {
					parseCommands(new StringTokenizer(inner), logoView);
				} 
			}
		}		
	}
}
