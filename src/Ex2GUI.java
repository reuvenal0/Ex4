import java.awt.*;
import java.io.IOException;

/**
 * ArielU. Intro2CS - 2025A
 * This is NOT a Junit class - as it tests GUI components which
 * should not be tested using Junit.
 * 
 * The Code uses the STDDraw class:
 * https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
 * Note: a few minor changes were added to STDDraw suit the logic of Ex2:
 * @author boaz.benmoshe
 *
 */
public class Ex2GUI {
	private static Sheet table; // this is the main data (an implementation of the Sheet interface).
	private static Index2D cord = null; // a table entry used by the GUI of setting up a cell value / form (For representing a focused cell selected for editing)
	public Ex2GUI() {;}  // an empty (redundant) constructor.

	/** The main function for running Ex2 */
	public static void main(String[] a) {
		table = new Ex2Sheet(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Building a table according to the sizes predefined in Ex2Utils
		testSimpleGUI(table); // Run the GUI's infinite loop
	}

	/**
	 * This function runs the main (endlees) loop of the GUI
	 * @param table the SpreadSheet - note: this class is written as a naive implementation of "singleton" (i.e., all static).
	 */
	public static void testSimpleGUI(Sheet table) {
		// init parameters - Set window size, scale, and line width for drawing:
		StdDrawEx2.setCanvasSize(Ex2Utils.WINDOW_WIDTH, Ex2Utils.WINDOW_HEIGHT);
		StdDrawEx2.setScale(0, Ex2Utils.MAX_X);
		StdDrawEx2.setPenRadius(Ex2Utils.PEN_RADIUS);
		StdDrawEx2.enableDoubleBuffering();

		table.eval();

		// endless loop (GUI)
		while (true) {
			StdDrawEx2.clear(); // clear the GUI (Ex2 window).
			drawFrame(); // draws the lines.
			drawCells(); // draws the cells
			StdDrawEx2.show(); // presents the window.
			int xx = StdDrawEx2.getXX(); // gets the x coordinate of the mouse click (-1 if none)
			int yy = StdDrawEx2.getYY(); // gets the y coordinate of the mouse click (-1 if none)
			inputCell(xx,yy); 			 // if isIn(xx,yy) an input window will be opened to allow the user to edit cell (xx,yy);
			StdDrawEx2.pause(Ex2Utils.WAIT_TIME_MS); // waits a few milliseconds - say 30 fps is sufficient.
		}
	}

	/**
	 * Saves the current state of the spreadsheet to a file.
	 * - Only non-empty cells are saved.
	 * - The saved file includes the coordinates and content of each cell.
	 * - using of course save method from Ex2Sheet.
	 * @param fileName The name (or path) of the file to save the spreadsheet to.
	 */
	public static void save(String fileName){
		try {
			table.save(fileName); // Calls the save method from Ex2Sheet
		}
		catch (IOException e) {
			e.printStackTrace(); // Prints the error if saving fails
		}
	}

	/**
	 * Loads the state of a spreadsheet from a file.
	 * - Clears the current spreadsheet before loading.
	 * - Recalculates all cells after loading the new data.
	 * - using of course load method from Ex2Sheet.
	 * @param fileName The name (or path) of the file to load the spreadsheet from.
	 */
	public static void load(String fileName){
		try {
			table.load(fileName); // Calls the load method from Ex2Sheet
		}
		catch (IOException e) {
			e.printStackTrace(); // Prints the error if loading fails
		}
	}

	/**
	 * Returns a Color corresponding to the type of the cell.
	 * - This method is used to visually distinguish different types of cells.
	 * - Each type of cell is displayed in a different color.
	 * @param t The type of the cell (defined in Ex2Utils).
	 * @return The Color associated with the cell type.
	 */
	private static Color getColorFromType(int t) {
		Color ans = Color.GRAY; // Default color for unknown types AND TEXT
		if(t== Ex2Utils.NUMBER) {ans=Color.black;} // Numbers are black
		if(t== Ex2Utils.FORM) {ans=Color.BLUE;} // Formulas are blue (valid)
		if(t== Ex2Utils.ERR_FORM_FORMAT) {ans=Color.RED;} // Formulas format errors are red
		if(t== Ex2Utils.ERR_CYCLE_FORM) {ans= StdDrawEx2.BOOK_RED;} // Formulas circular errors are dark red
		if(t== Ex2Utils.FUCN_TYPE) {ans = StdDrawEx2.GREEN;} // Functions are green (valid)
		if(t== Ex2Utils.IF_TYPE) {ans = StdDrawEx2.YELLOW;} // Conditions (IF) are yellow (valid)
		if(t== Ex2Utils.ERR_FUNC) {ans = StdDrawEx2.PINK;} // Function errors are pink
		if(t== Ex2Utils.ERR_IF) {ans = StdDrawEx2.MAGENTA;} // Conditions (IF) errors are magenta
		return ans;
	}

	/**
	 * Draws the grid lines of the spreadsheet.
	 * - Displays column labels (A-Z) and row numbers.
	 * - Uses black lines for the grid.
	 */
	private static void drawFrame() {
		StdDrawEx2.setPenColor(StdDrawEx2.BLACK); // Set grid color to black
		int max_y = table.height();  // Number of rows in the spreadsheet
		double x_space = Ex2Utils.GUI_X_SPACE, x_start = Ex2Utils.GUI_X_START;
		double y_height = Ex2Utils.GUI_Y_TEXT_START;

		// Loop through each row to draw grid lines and labels
		for (int y = 0; y < max_y; y = y + 1) {
			double xs = y * x_space;
			double xc = x_start + y * x_space;

			// Draw horizontal and vertical grid lines
			StdDrawEx2.line(0, y + 1, Ex2Utils.MAX_X, y + 1);
			StdDrawEx2.line(xs, 0, xs, max_y);

			// Calculate row number (rows are displayed in reverse order)
			int yy = max_y - (y + 1);

			// Display row numbers on the left
			StdDrawEx2.text(1, y + y_height, "" + (yy));

			// Display column letters (A-Z) on the top
			StdDrawEx2.text(xc, max_y + y_height, "" + Ex2Utils.ABC[y]);
		}
	}

	/**
	 * Draws the content of each cell in the spreadsheet.
	 * - Only non-empty cells are displayed.
	 * - Each type of cell is displayed in a different color.
	 */
	private static void drawCells() {
		StdDrawEx2.setPenColor(StdDrawEx2.BLACK); // Default text color
		int max_y = table.height();
		int maxx = table.width();
		double x_space = Ex2Utils.GUI_X_SPACE, x_start = Ex2Utils.GUI_X_START;
		double y_height = Ex2Utils.GUI_Y_TEXT_START;

		// Loop through each cell in the spreadsheet
		for (int x = 0; x < maxx; x = x + 1) {
			double xc = x_start + x * x_space;
			for (int y = 0; y < max_y; y = y + 1) {
				// Get the displayed value of the cell
				String w = table.value(x, y);//""+abc[x]+y;

				Cell cc = table.get(x, y);
				// Get the type of the cell
				int t = cc.getType();
				// Set the text color based on the type of the cell
				StdDrawEx2.setPenColor(getColorFromType(t));

				// Limit the length of the displayed text to the maximum allowed
				int max = Math.min(Ex2Utils.MAX_CHARS, w.length());
				w = w.substring(0, max);

				// Calculate the y-coordinate for the text (rows are displayed in reverse order)
				double yc = max_y - (y + 1 - y_height);

				// Display the text in the cell
				StdDrawEx2.text(xc, yc, w);
			}
		}
	}

	/**
	 * Opens an input window to edit the content of a cell.
	 * - Only opens if the coordinates are within the spreadsheet.
	 * - Recalculates the spreadsheet after updating the cell.
	 *
	 * @param xx The x-coordinate of the clicked cell.
	 * @param yy The y-coordinate of the clicked cell.
	 */
	private static void inputCell(int xx,int yy) {
		// Check if the clicked coordinates are within the spreadsheet
		if(table.isIn(xx,yy)) {
			Cell cc = table.get(xx,yy);

			// I think the following line should be added, Ido says there is no problem:
			cord = new CellEntry(xx,yy); // Store the coordinates of the clicked cell

			// Display the current content of the cell for editing
			String ww = cord+": "+cc.toString()+" : ";
			StdDrawEx2.text(Ex2Utils.GUI_X_START, Ex2Utils.MAX_X-1, ww);
			StdDrawEx2.show();
			if(Ex2Utils.Debug) {System.out.println(ww);}

			// Get the new content from the user
			String c = StdDrawEx2.getCell(cord,cc.getData());
			String s1 = table.get(xx,yy).getData();

			// If the user didn't change the content, keep the old value
			if(c==null) {
				table.set(xx,yy,s1);
			}
			else {
				table.set(xx, yy, c);
				int[][] calc_d = table.depth();
				if (calc_d[xx][yy] == Ex2Utils.ERR) {
					table.get(xx,yy).setType(Ex2Utils.ERR_CYCLE_FORM);
				}
			}

			// Recalculate all cells after the change
			table.eval();
			// Reset the mouse click coordinates
			StdDrawEx2.resetXY();
		}
	}
}
