// Erin Clark
// Capstone Project: Extended Logo
// User Interface class
// 12/1/2008

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.*;

public class elogo extends JFrame
{
	private class commandHandler implements ActionListener
	{
		private StringTokenizer commandTokens;

		// when the user presses enter, this will take the entered in
		// commands and call the command parser
		public void actionPerformed(ActionEvent e)
		{
			commandTokens = new 
				StringTokenizer(e.getActionCommand());
			parseCommands(commandTokens);
			elogoTF.setText("");
		}
	}

	// takes in commands and handles them appropriately
	private void parseCommands(StringTokenizer commandTokens)
	{
		String currentToken;
		String next;
		while (commandTokens.hasMoreTokens())
		{
			currentToken = commandTokens.nextToken();
			// since Java never implemented switch on a string, 
			// i must type in if(currentToken.equals...)
			// for each command
			if (currentToken.equals("fd")) {
				currentToken = commandTokens.nextToken();
				eCanvas.fd(Integer.parseInt(currentToken));
			}
			else if (currentToken.equals("rt")) {
				currentToken = commandTokens.nextToken();
				eCanvas.rt(Integer.parseInt(currentToken));
			}
			else if (currentToken.equals("lt")) {
				currentToken = commandTokens.nextToken();
				eCanvas.lt(Integer.parseInt(currentToken));
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
					eCanvas.color(
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
					eCanvas.color(colorNum);
				}
			}
			else if(currentToken.equals("ellipse")) {
				currentToken = commandTokens.nextToken();
				next = commandTokens.nextToken();
				eCanvas.ellipse(Integer.parseInt(currentToken),
					       	Integer.parseInt(next));
			}
			else if(currentToken.equals("circle")) {
				currentToken = commandTokens.nextToken();
				eCanvas.circle(Integer.parseInt(currentToken));
			}
			else if(currentToken.equals("rect")) {
				currentToken = commandTokens.nextToken();
				eCanvas.rect(Integer.parseInt(currentToken));
			}
			else if(currentToken.equals("north")) {
				eCanvas.north();
			}
			else if(currentToken.equals("east")) {
				eCanvas.east();
			}
			else if(currentToken.equals("south")) {
				eCanvas.south();
			}
			else if(currentToken.equals("west")) {
				eCanvas.west();
			}
			else if(currentToken.equals("clear")) {
				eCanvas.clear();
			}
			else if(currentToken.equals("pu")) {
				eCanvas.penup();
			}
			else if(currentToken.equals("pd")) {
				eCanvas.pendown();
			}
			else if(currentToken.equals("ht")) {
				eCanvas.hideTurtle();
			}
			else if(currentToken.equals("st")) {
				eCanvas.showTurtle();
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
					parseCommands(new StringTokenizer(inner));
				} 
			}
		}
	}

	private JTextField elogoTF;
	private elogoCanvas eCanvas;
	private commandHandler handler;
	
	// set up the gui interface
	public elogo()
	{
		elogoTF = new JTextField(20);
		eCanvas = new elogoCanvas();
		eCanvas.setBackground(Color.white);
		eCanvas.setForeground(Color.white);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add("Center", eCanvas);
		c.add("South", elogoTF);
		handler = new commandHandler();
		elogoTF.addActionListener(handler);
		setTitle("elogo");
		setVisible(true);
		setSize(360,380);	 // 360x380 window
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}


	public static void main(String args[])
	{
		elogo e = new elogo();
	}
}

