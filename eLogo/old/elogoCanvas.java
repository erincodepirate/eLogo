// Erin Clark
// Capstone Project: Extended Logo
// Logo canvas class
// 12/1/2008

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.Math;

public class elogoCanvas extends Canvas
{
	private final double startCoord = 180;
	private double turtleX = startCoord;
	private double turtleY = startCoord;
	private boolean hideTurtle = false;
	private boolean penup = false;
	private Color fgColor = Color.black;
	private Color bgColor = Color.white;
	private final BasicStroke stroke = new BasicStroke(1.0f);
	private double theta = 3*Math.PI/2;
	private ArrayList<Shape> moves = new ArrayList<Shape>();
	private ArrayList<Color> colors = new ArrayList<Color>();
	Graphics2D g2;

	// Draws the turtle on the canvas when called, the head faces
	// the direction that the turtle is going to draw in the event of
	// an fd or rect
	public void drawTurtle()
	{
		Ellipse2D.Double body = new 
			Ellipse2D.Double(turtleX-5, turtleY-5, 10, 10);
		double headX = turtleX + 5 * Math.cos(theta) - 2.5;
		double headY = turtleY + 5 * Math.sin(theta) - 2.5;
		Ellipse2D.Double head = new 
			Ellipse2D.Double(headX, headY, 5, 5);
		g2.setPaint(fgColor);
		g2.draw(body);
		g2.draw(head);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(stroke);

		for (int i = 0; i < moves.size(); i++)
		{
			// paint on all the moves done so far
			g2.setPaint(colors.get(i));
			g2.draw(moves.get(i));
		}
		
		// paint the turtle if it is not hidden
		if (!hideTurtle) {
			drawTurtle();
		}
	}

	// adds a line on the list of shapes with its associated color
	// and then repaints to draw it
	public void fd(int dist) 
	{
		//Dimension d = this.getSize();

		double newX = turtleX + dist * Math.cos(theta);
		double newY = turtleY + dist * Math.sin(theta);
		if (!penup) {
			moves.add(new 
				Line2D.Double(turtleX, turtleY, newX, newY));
			colors.add(fgColor);
		}
		turtleX = newX;
		turtleY = newY;
		repaint();
	}

	// turn right, both left and right are backwards mathematically
	// since Java just seems to work that way
	public void rt(int deg)
	{
		theta += Math.toRadians(deg);
		while (theta >= 2 * Math.PI)
			theta -= 2 * Math.PI;
		repaint();
	}

	// turn left
	public void lt(int deg)
	{
		theta -= Math.toRadians(deg);
		while (theta < 0)
			theta += 2 * Math.PI;
		repaint();
	}

	// select a color based on its assigned number, allows iterating
	// over colors
	public void color(int num)
	{
		switch (num % 10) {
			case 0:
				fgColor = Color.white;
				break;
			case 1:
				fgColor = Color.black;
				break;
			case 2:
				fgColor = Color.red;
				break;
			case 3:
				fgColor = Color.green;
				break;
			case 4:
				fgColor = Color.blue;
				break;
			case 5:
				fgColor = Color.yellow;
				break;
			case 6:
				fgColor = Color.orange;
				break;
			case 7:
				fgColor = Color.pink;
				break;
			case 8:
				fgColor = Color.magenta;
				break;
			case 9:
				fgColor = Color.gray;
				break;
		}
		repaint();
	}
	
	// draws a that touches the turtle's point with the given radius
	public void circle(int radius)
	{
		if (!penup) {
			double startX = turtleX + radius * Math.cos(theta)
				- radius;
			double startY = turtleY + radius * Math.sin(theta)
				- radius;
			double diameter = radius * 2;
			moves.add(new Ellipse2D.Double(
				startX, startY, diameter, diameter));
			colors.add(fgColor);
			repaint();
		}
	}
	
	// draws an ellipse centered at the turtle with the given
	// height and width
	public void ellipse(int height, int width)
	{
		if (!penup) {
			double startX = turtleX - width/2;
			double startY = turtleY - height/2;
			moves.add(new Ellipse2D.Double(
				startX, startY, width, height));
			colors.add(fgColor);
			repaint();
		}
	}

	
	// draws a rectangle starting from the turtle's point
	// to the end of the distance entered, works like ellipse
	public void rect(int dist)
	{
		if (!penup) {
			double width = Math.abs(dist * Math.cos(theta));
			double height = Math.abs(dist * Math.sin(theta));
			// defaults, works for south west
			double startX = turtleX;
			double startY = turtleY;
			// north west, PI < theta < 3PI/2
			if (theta > Math.PI && theta < 3 * Math.PI/2) {
				startX = turtleX - width;
				startY = turtleY - height;
			}
			// north east, 3PI/2 <= theta < 2PI
			else if (theta >= 3 * Math.PI/2 && theta < 2 * Math.PI) {
				startY = turtleY - height;
			}
			// south west, PI/2 < theta <= PI
			else if (theta > Math.PI/2 && theta <= Math.PI) {
				startX = turtleX - width;
			}

			moves.add(new Rectangle2D.Double(
				startX, startY, width, height));
			colors.add(fgColor);
			repaint();
		}
	}

	// Java coordinates are upside down so north is 3PI/2 and south is PI/2
	public void north()
	{
		theta = 3 * Math.PI/2;
		repaint();
	}

	public void east() 
	{
		theta = 0;
		repaint();
	}

	public void south()
	{
		theta = Math.PI/2;
		repaint();
	}

	public void west()
	{
		theta = Math.PI;
		repaint();
	}

	// set everything back to its defaults
	public void clear()
	{
		turtleX = startCoord;
		turtleY = startCoord;
		moves.clear();
		colors.clear();
		fgColor = Color.black;
		theta = 3 * Math.PI/2;
		hideTurtle = false;
		penup = false;
		repaint();
	}

	public void penup()
	{
		penup = true;
	}

	public void pendown()
	{
		penup = false;
	}

	public void hideTurtle()
	{
		hideTurtle = true;
		repaint();
	}

	public void showTurtle()
	{
		hideTurtle = false;
		repaint();
	}
}

