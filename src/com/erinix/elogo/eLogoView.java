package com.erinix.elogo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

// This class implements the canvas portion of the eLogo interpreter
@SuppressLint("DrawAllocation")
public class eLogoView extends ImageView {
	private Paint paint = new Paint();
	private Context context;
	private static float origin = -1;
	private float turtleX = origin;
	private float turtleY = origin;
	private int xMin = 0;
	private int yMin = 0;
	private int xMax;
	private int yMax;
	private double theta = 3 * Math.PI / 2;
	private static int initialFgColor = Color.BLACK;
	private int fgColor = initialFgColor;
	private static int initialBgColor = Color.WHITE;
	private int bgColor = initialBgColor;
	private boolean hideTurtle = false;
	private boolean penup = false;
	private ArrayList<Path> moves = new ArrayList<Path>();
	private ArrayList<Integer> colors = new ArrayList<Integer>();

	// constructor
	public eLogoView(Context con, AttributeSet attrs) {
		super(con, attrs);
		context = con;
		new Handler();
	}
	
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		// Set the boundaries for the canvas
		xMax = w - 1;
		yMax = h - 1;
		if (turtleX == origin && turtleY == origin) {
			initialize(); // now that we know the boundaries we can set the
							// turtle's initial coordinates
		}
	}

	// onDraw function performs all of the calls to draw to canvas
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(bgColor); // fill the canvas with the background color
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);

		for (int i = 0; i < moves.size(); i++) {
			paint.setColor(colors.get(i));
			canvas.drawPath(moves.get(i), paint);
		}

		if (!hideTurtle) {
			drawTurtle(canvas);
		}
	}

	// Function to draw the turtle icon on the screen in its current location,
	// rotation and color
	public void drawTurtle(Canvas canvas) {
		Matrix matrix = new Matrix();

		// load the image for the turtle cursor
		BitmapDrawable turtle = (BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.turtle);

		// create a mutable bitmap copy of the original picture so it's color
		// can be changed if necessary
		Bitmap turtleBitmap = turtle.getBitmap().copy(Bitmap.Config.ARGB_8888,
				true);

		int w = turtleBitmap.getWidth();
		int h = turtleBitmap.getHeight();

		// if the current fgColor is something other than the turtle image's
		// default color of black then
		// change the turtle's color to match
		if (fgColor != Color.BLACK) {
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					int color = turtleBitmap.getPixel(x, y);

					// change all the black pixels to the current fgColor
					if (color == Color.BLACK) {
						turtleBitmap.setPixel(x, y, fgColor);
					}
				}
			}
		}

		// adjust the coordinates for printing the image from the upper left
		// hand corner
		float x = turtleX - w / 2;
		float y = turtleY - h / 2;

		// rotate, move and display the turtle cursor
		matrix.postRotate((float) Math.toDegrees(theta) + 90);
		matrix.postTranslate(x, y);
		canvas.drawBitmap(turtleBitmap, matrix, paint);
	}

	public void initialize() {
		turtleX = xMax / 2;
		turtleY = yMax / 2;
	//	Log.i("isTurtleAtOrigin", "" + turtleX + turtleY);
	}

	public void fd(int dist) {
		// use good ole fashioned trig to get the new coordinates from the
		// current position with the given distance of the line
		float newX = (float) (turtleX + dist * Math.cos(theta));
		float newY = (float) (turtleY + dist * Math.sin(theta));
		float currentX = turtleX;
		float currentY = turtleY;
		float nextX = turtleX;
		float nextY = turtleY;
		float segmentX = newX;
		float segmentY = newY;
		boolean isWrappedAround;
		Path line = new Path();
		float slope = 0;
		float intercept = 0; // initialize to a fake number
		
		// calculate the slope and intercept
		if (turtleX != newX) {
			slope = (turtleY - newY) / (turtleX - newX);
		}
		
		// check each of the conditions of wrapping around and
		// draw lines using adjusted coordinates until it is no longer
		// wrapped around
		do {
			// calculate the intercept if the slope is defined
			if (turtleX != newX) {
				intercept = currentY - slope * currentX;
			}
			Log.w("theta", "" + theta);
			
			// check to see if the new line hits any of the borders
			if (newX >= xMax || newY >= yMax || newX < xMin || newY < yMin) {
				isWrappedAround = true;
				
				// check to see which half of the canvas the turtle is facing
				if (theta <= 3 * Math.PI / 2 && theta > Math.PI / 2) {
					Log.w("xMin", newX + "," + newY);
					// facing xMin
					if (turtleX != newX) {
						// assume this line hits the x minimum
						segmentY = slope * xMin + intercept;

						// in the event this line hits the y maximum or minimum
						// before the x minimum, the recently calculated segmentY
						// will be out of bounds, so now we must solve for X
						if (segmentY >= yMax) {
							segmentX = (yMax - intercept) / slope;
							segmentY = yMax;
							
							// the next part of this line will start at the y minimum
							nextY = yMin;
							
							// since Y gets shifted down by yMax amount, so does newY
							newY = newY - yMax;
							
							// if the line hits the x min and y max corner then the next
							// x coordinate will be the x minimum, otherwise x stays the same
							if (segmentX == xMin) {
								nextX = xMax;
								
								// since X gets shifted over by xMax amount, so does newX
								newX = newX + xMax;
							} else {
								nextX = segmentX;
							}				
						} else if (segmentY < yMin) {
							segmentX = (yMin - intercept) / slope;
							segmentY = yMin;
							
							// the next part of this line will start at the y maximum
							nextY = yMax;
							
							// since Y gets shifted up by yMax amount, so does newY
							newY = newY + yMax;
							
							// if the line hits the x min and y min corner then the next
							// x coordinate will be the x maximum, otherwise x stays the same
							if (segmentX == xMin) {
								nextX = xMax;
								
								// since X gets shifted over by xMax amount, so does newX
								newX = newX + xMax;
							} else {
								nextX = segmentX;
							}				
							
						} else { // otherwise this line does in fact hit the x minimum
							segmentX = xMin;
							
							// the start of the next line segment will begin at the X minimum
							// with the same Y;
							nextY = segmentY;
							nextX = xMax;
							
							// since X gets shifted over by xMax amount, so does newX
							newX = newX + xMax;
						}						
					} else { // vertical line facing upward
						segmentY = yMax;
						nextY = yMin;
						newY = newY + yMax;
					}		
				} else {
					Log.w("xMax", newX + "," + newY);
					// facing xMax
					if (turtleX != newX) {
						// assume this line hits the x minimum
						segmentY = slope * xMax + intercept;

						// in the event this line hits the y maximum or minimum
						// before the x minimum, the recently calculated segmentY
						// will be out of bounds, so now we must solve for X
						if (segmentY >= yMax) {
							segmentX = (yMax - intercept) / slope;
							segmentY = yMax;
							
							// the next part of this line will start at the y minimum
							nextY = yMin;
							
							// since Y gets shifted down by yMax amount, so does newY
							newY = newY - yMax;
							
							// if the line hits the x max and y max corner then the next
							// x coordinate will be the x minimum, otherwise x stays the same
							if (segmentX == xMax) {
								nextX = xMin;
								
								// since X gets shifted over by xMax amount, so does newX
								newX = newX - xMax;
							} else {
								nextX = segmentX;
							}				
						} else if (segmentY < yMin) {
							segmentX = (yMin - intercept) / slope;
							segmentY = yMin;
							
							// the next part of this line will start at the y maximum
							nextY = yMax;
							
							// since Y gets shifted up by yMax amount, so does newY
							newY = newY + yMax;
							
							// if the line hits the x max and y min corner then the next
							// x coordinate will be the x maximum, otherwise x stays the same
							if (segmentX == xMax) {
								nextX = xMax;
								
								// since X gets shifted over by xMax amount, so does newX
								newX = newX - xMax;
							} else {
								nextX = segmentX;
							}				
							
						} else { // otherwise this line does in fact hit the x minimum
							segmentX = xMax;
							
							// the start of the next line segment will begin at the X minimum
							// with the same Y;
							nextY = segmentY;
							nextX = xMin;
							
							// since X gets shifted over by xMax amount, so does newX
							newX = newX - xMax;
						}						
					} else { // vertical line facing downward
						segmentY = yMin;
						nextY = yMax;
						newY = newY - yMax;
					}
				}				
			} else {
				isWrappedAround = false;
			}

			if (!penup) {
				//add either the line or a segment of it if wrapped around
				line.moveTo(currentX, currentY);
				line.lineTo(segmentX, segmentY);
				moves.add(line);
				colors.add(fgColor);
			}
			// reset our variables for the next iteration
			if (isWrappedAround) {
				currentX = nextX;
				currentY = nextY;
				segmentX = newX;
				segmentY = newY;
			}
		} while (isWrappedAround);
		
		// move the turtle to it's new position
		turtleX = newX;
		turtleY = newY;
		// redraw everything after the command has been run
		invalidate();
	}

	// turn right, both left and right are backwards mathematically
	// since Java just seems to work that way
	public void rt(int deg) {
		theta += Math.toRadians(deg);
		while (theta >= 2 * Math.PI)
			theta -= 2 * Math.PI;
		invalidate();
	}

	// turn left
	public void lt(int deg) {
		theta -= Math.toRadians(deg);
		while (theta < 0)
			theta += 2 * Math.PI;
		invalidate();
	}

	// select a color based on its assigned number (android version
	// just uses android native color codes
	public void color(int num) {
		fgColor = num;
		invalidate();
	}

	// draws a that touches the turtle's point with the given radius
	public void circle(int radius) {
		if (!penup) {
			float startX = (float) (turtleX + radius * Math.cos(theta));
			float startY = (float) (turtleY + radius * Math.sin(theta));
			// float diameter = radius * 2;
			Path circle = new Path();
			// moves.add(new Ellipse2D.Double(
			// startX, startY, diameter, diameter));
			circle.addCircle(startX, startY, radius, Path.Direction.CCW);
			moves.add(circle);
			colors.add(fgColor);
			invalidate();
		}
	}

	// Java coordinates are upside down so north is 3PI/2 and south is PI/2
	public void north() {
		theta = 3 * Math.PI / 2;
		invalidate();
	}

	public void east() {
		theta = 0;
		invalidate();
	}

	public void south() {
		theta = Math.PI / 2;
		invalidate();
	}

	public void west() {
		theta = Math.PI;
		invalidate();
	}

	// set everything back to its defaults
	public void clear() {
		initialize();
		moves.clear();
		colors.clear();
		fgColor = Color.BLACK;
		theta = 3 * Math.PI / 2;
		hideTurtle = false;
		penup = false;
		invalidate();
	}

	public void penup() {
		penup = true;
	}

	public void pendown() {
		penup = false;
	}

	public void hideTurtle() {
		hideTurtle = true;
		invalidate();
	}

	public void showTurtle() {
		hideTurtle = false;
		invalidate();
	}

}
