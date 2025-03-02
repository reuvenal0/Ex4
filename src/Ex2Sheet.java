import java.io.*;
import java.util.*;

/**
 * Ex2Sheet - 2D Spreadsheet Implementation
 * - Manages cells containing text, numbers, formulas, conditions, and functions.
 * - Supports advanced calculations with error handling for circular references, invalid formulas, and more.
 * - Automatically recalculates cell values when dependencies change.
 * - Saves and loads spreadsheet data from files.
 * Limitations:
 * - todo: Arithmetic operations on a value that repeats in the same  functions cell and more
 */

public class Ex2Sheet implements Sheet {
    Cell[][] table; // 2D array of Cells

    /**
     * constructor for an Ex2Sheet object - 2D spreadsheet
     * Creating a table according to the dimensions obtained:
     * @param x - The X dimension of the spreadsheet
     * @param y - The X dimension of the spreadsheet
     */
    public Ex2Sheet(int x, int y) {
        if (x < 0 || x > 26 || y < 0|| y > 100) throw new IllegalArgumentException("invalid value");

        table = new SCell[x][y]; // Initializes the arrays containing the various cells

        // We will initialize the various cells to have empty text
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("");
            }
        }

        //We will use our calculation function to initialize all the cells
        eval();
    }

    /**
     * Default constructor -
     * Creates a spreadsheet with predefined dimensions (according to the definitions in Ex2Utils) using our constructor:
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    /**
     * Return the Cell in the x,y, position (or null if not in).
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return the cell in the x,y coordinate (or null if not in).
     */
    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) return null;
        return table[x][y];
    }

    /**
     * Return the cell @ the coordinate (entry). E.g., the String "B3" will be translated to [1][3].
     * @param entry Cord of the requested cell
     * @return the cell at the X.Y coordinate, or null if cords is an illegal coordinate or is out of this SprayedSheet.
     */
    @Override
    public Cell get(String entry) {
        CellEntry ce = new CellEntry(entry);
        if (!ce.isValid()) return null;
        return get(ce.getX(), ce.getY());
    }

    /**
     * This method changes the x,y cell to a cell with the data s.
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @param s - the string representation of the cell.
     */
    @Override
    public void set(int x, int y, String s) {
        // If the requested coordinates are not in the spreadsheet - we will end the method.
        if (!isIn(x, y)) return;

        // We will create a new cell with the desired data.
        Cell c = new SCell(s);
        // We will insert the cell we created into our cell arrays that represents the spreadsheet
        table[x][y] = c;

        // recalculate all cells in the spreadsheet following the change we made:
        eval();
    }

    /**
     * @return the dimension (length) of the x-coordinate of this spreadsheet.
     */
    @Override
    public int width() {
        return table.length;
    }

    /**
     * @return the dimension (length) of the y-coordinate of this spreadsheet.
     */
    @Override
    public int height() {
        return table[0].length;
    }

    /**
     * Check is the x,y coordinate is with in this table.
     * @param x - integer, x-coordinate of the table (starts with 0).
     * @param y - integer, y-coordinate of the table (starts with 0).
     * @return true iff the x,y coordinate is a valid entry (cell) with in this spreadsheet.
     */
    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    /**
     * This method calculates the value that is displayed to the user in a particular cell -
     * not the raw data that the cell contains.
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return the string that will be presented in the x,y entry.
     */
    @Override
    public String value(int x, int y)
    {
        String ans = Ex2Utils.EMPTY_CELL; // Default value - cell is empty

        // First, let's acknowledge that we received coordinates for a cell located inside our spreadsheet
        if (isIn(x, y))
        {
            // The cell is inside the table, let's drag it to us
            Cell c = get(x,y);

            // If the cell is not empty, we continue:
            if (c!=null) {
                // Let's first see if this cell was marked with an error during a previous calculation:

                if (c.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                // If there is an error in calculating the formula, then we will print the prefix error String:
                    return Ex2Utils.ERR_FORM;
                }
                else if (c.getType() == Ex2Utils.ERR_CYCLE_FORM) {
                // If there is a circularity error in the formula inside this Cell then we will print the prefix error String:
                    return Ex2Utils.ERR_CYCLE;
                } else if (c.getType() == Ex2Utils.ERR_FUNC) {
                    // If there is a function error inside this Cell then we will print the prefix error String:
                    return Ex2Utils.ERR_FUCN_str;
                }
                else if (c.getType() == Ex2Utils.ERR_IF) {
                    // If there is an IF error inside this Cell then we will print the prefix error String:
                    return Ex2Utils.ERR_IF_str;
                }

                // We will print the value if the data inside the cell is not empty.
                if (c.toString() != null)
                {
                    // Let's see if there is a depth error in the formula cells calculation now:
                    if ((c.getOrder() == Ex2Utils.ERR_CYCLE_FORM) && (c.getType() == Ex2Utils.FORM)) {
                        // If there is a circularity error in the formula inside this Cell then we will print the prefix error String:
                        c.setType(Ex2Utils.ERR_CYCLE_FORM);
                        ans = Ex2Utils.ERR_CYCLE;
                        return ans;
                    }
                    // Calculate the cell contents:
                    ans = eval(x, y);
                    // If we get null we will print an empty string.
                    if (ans == null) ans = Ex2Utils.EMPTY_CELL;
                }
            }
        }
        // We will return the result:
        return ans;
    }

    /**
     * computes all the values of all the cells in this spreadsheet.
     */
    @Override
    public void eval() {
        // just for convenience, we will prepare our table size in advance:
        int width = width();
        int height = height();

        // Calculate the depths (dependency) of all the cells in our spreadsheet:
        int[][] depths = depth();

        // Find maximum depth for non-cyclic cells
        int maxDepth = 0; // We will start counting from 0 (a cell with no independence at all) and see if a larger value is found
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (depths[i][j] > maxDepth) {
                    maxDepth = depths[i][j];
                }
            }
        }

        // Reset all formula cells to their original type - This way we can update in case there are now calculation errors:
        // We will only need to update the type in cells that may have an error: formulas, conditions, and functions - there is nothing to change about text and numbers.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = get(i, j);
                if (cell != null && cell.getData() != null && !cell.getData().isEmpty()) {
                    // If it starts with '=if(', it's a condition (until proven otherwise - In case we get an error in the calculation)
                    if (cell.getData().matches("(?i)^=if\\(.*"))
                    {
                        cell.setType(Ex2Utils.IF_TYPE);
                    }

                    // If it starts with '=<function>(', it's a function (until proven otherwise - In case we get an error in the calculation)
                    if (Arrays.stream(Ex2Utils.FUNCTIONS).anyMatch(func -> cell.getData().matches("(?i)^=" + func + "\\(.*"))) {
                        cell.setType(Ex2Utils.FUCN_TYPE);
                    }

                    // If it starts with **just** '=', it's a formula (until proven otherwise - In case we get an error in the calculation)
                    if (cell.getData().startsWith("=") &&
                            (!cell.getData().matches("(?i)^=if\\(.*")) &&
                            (Arrays.stream(Ex2Utils.FUNCTIONS).noneMatch(func -> cell.getData().matches("(?i)^=" + func + "\\(.*"))) )
                    {
                        cell.setType(Ex2Utils.FORM);
                    }
                }
            }
        }

        // Evaluate cells level by level based on their depth,
        // We will use three loops for this:
        for (int currentDepth = 0; currentDepth <= maxDepth; currentDepth++) // depth loop
        {
            for (int i = 0; i < width; i++) // X-cord (width) loop
            {
                for (int j = 0; j < height; j++) // Y-cord (height) loop
                {
                    if (depths[i][j] == currentDepth)
                    {
                        // extract the cell's raw data:
                        Cell cell = get(i, j);
                        if (cell != null)
                        {
                            // Let's check the cells marked with a circular error, and mark them with the error appropriate to their cell type.
                            if (depths[i][j] == Ex2Utils.ERR_CYCLE_FORM)
                            {
                                // We have defined that any circular error in the case where the cell is a condition or a function - will be defined as an error for that particular cell type, and not just a circular error.
                                //Therefore, in the case where the cell type in the source is a condition or a function, we will mark a circular error that it has as a specific error for it

                                if (table[i][j].getType() == Ex2Utils.IF_TYPE || table[i][j].getType() == Ex2Utils.ERR_IF) {
                                    //  we have a condition cell with a circularity error, we will mark it accordingly - ERR_IF:
                                    table[i][j].setType(Ex2Utils.ERR_IF);
                                    table[i][j].setOrder(Ex2Utils.ERR_IF);
                                }
                                else if (table[i][j].getType() == Ex2Utils.FUCN_TYPE) {
                                    //  we have a function cell with a circularity error, we will mark it accordingly - ERR_FUNC:
                                    table[i][j].setType(Ex2Utils.ERR_FUNC);
                                    table[i][j].setOrder(Ex2Utils.ERR_FUNC);
                                }
                                else {
                                    //  we have a formula cell with a circularity error, we will mark it accordingly- ERR_CYCLE_FORM:
                                    cell.setOrder(Ex2Utils.ERR_CYCLE_FORM);
                                    cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                                }
                            }
                            // If there is no circularity error, we will check that we can calculate the cell correctly, and mark its depth within the cell fields.
                            else {
                                // Let's try to calculate the value in the cell:
                                String calculated = eval(i, j);
                                cell.setOrder(currentDepth); // Set the Cell order
                                // The string calculated is temporary just so we can see that we can calculate the cell according to its depth correctly.
                                // We do not use the data that comes out of the EVAL method, to get the final calculated value we have the VALUE method.
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     *  Computes a 2D array of the same dimension as this SpreadSheet, each entry holds its dependency depth.
     *  if a cell is not dependent on any other cell its depth is 0.
     *  else assuming the cell depends on cell_1, cell_2... cell_n, the depth of a cell is
     *  1+max(depth(cell_1), depth(cell_2), ... depth(cell_n)).
     *  In case a cell os a circular dependency (e.g., c1 depends on c2 & c2 depends on c1) its depth should be -1.
     */
    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];

        // Default value: Initialize all cells to -1 to indicate they are unprocessed yet
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -1;
            }
        }

        int depth = 0; // Tracks the current depth level
        int count = 0; // Counts processed cells
        int max = width() * height(); // Total number of cells in our SpreadSheet
        boolean flagC = true; // Indicates if any cell was processed in the last iteration

        // Continue while there are cells left to process and changes are still occurring
        while (count < max && flagC) {
            flagC = false; // We will initialize the variable - until one cell is calculated

            // Iterate through all cells in the spreadsheet
            for (int x = 0; x < width(); x++) { // width - X loop
                for (int y = 0; y < height(); y++) { // height - Y loop
                    Cell cell = get(x, y); // getting the cell in location [x][y]

                    // Skip if cell is null or Text (empty cell data string is set to text) or Number - set depth as 0:
                    if (cell == null || cell.getType() == Ex2Utils.NUMBER || cell.getType() == Ex2Utils.TEXT) {
                        if (ans[x][y] == -1)
                        {
                            ans[x][y] = 0; // A cell of these types must have a depth of 0

                            // We calculated a cell
                            count++;
                            flagC = true;
                        }
                        continue; // moving to the next loop iteration - the next cell, since we confirm it depth
                    }

                    // If this cell is still unprocessed (-1) and not text or number:
                    if (ans[x][y] == -1)
                    {
                        // Extract the formula
                        String cell_data = cell.getData().substring(1);
                        boolean canProcess = true; // temp bool just for this cell depth calculation
                        int maxDepth = 0; // Track the maximum depth of dependencies

                        // Iterate the cell string to find all referenced cells
                        for (int i = 0; i < cell_data.length(); i++) {
                            if (Character.isLetter(cell_data.charAt(i))) {
                                int endIndex = i + 1;
                                // Go over the formula (string) - finding the reference:
                                while (endIndex < cell_data.length() && Character.isDigit(cell_data.charAt(endIndex))) {
                                    endIndex++;
                                }

                                // We will extract the reference within the formula:
                                String ref = cell_data.substring(i, endIndex);
                                // convert the coordinates:
                                CellEntry ce = new CellEntry(ref);
                                int ce_x = ce.getX();
                                int ce_y = ce.getY();

                                // Make sure the coordinates are valid and refer to a cell that exists in our table:
                                if (ce.isValid() && isIn(ce_x,ce_y))
                                {
                                    // If any referenced cell is still unprocessed, we can't process this cell yet
                                    if (ans[ce_x][ce_y] == -1) {
                                        canProcess = false; // Dependency is unprocessed (in this iteration at least)
                                        break;
                                    }
                                    // Keep track of the maximum depth of referenced cells
                                    maxDepth = Math.max(maxDepth, ans[ce.getX()][ce.getY()]);
                                }
                                // Skipping the string loop to the end of the cell reference:
                                i = endIndex - 1;
                            }
                        }

                        // If the cell dependencies are processed, calculate this cell's depth
                        if (canProcess) {
                            ans[x][y] = maxDepth + 1;
                            count++;
                            flagC = true;
                        }
                    }
                }
            }
            // Add one to our depth counter - for the next iteration:
            depth++;

            // If we've gone through all possible depths without resolving all cells, we have a cycle:
            if (depth > max) {
                break; // closing the Cells while loop
            }
        }
        // We will make sure that all the cycled cells are marked as follows:
        // that their value in our depth array remains -1, meaning that we were unable to calculate it.
        // Changing their Cell type to:
        // - ERR_IF in case of IF cycle
        // - ERR_FUNC in case of function cycle
        // - ERR_CYCLE_FORM in case of formula cycle
        for (int x = 0; x < width(); x++) { // width - X
            for (int y = 0; y < height(); y++) { // height - Y
                if (ans[x][y] == -1)
                {
                    String getData_tmp = table[x][x].getData(); // getting cell string data

                    // case of IF cycle
                    if (getData_tmp.matches("(?i)^=if\\(.*"))
                    {
                        table[x][y].setType(Ex2Utils.ERR_IF);
                        table[x][y].setOrder(Ex2Utils.ERR_IF);
                    }

                    // case of function cycle
                    else if (Arrays.stream(Ex2Utils.FUNCTIONS).anyMatch(func -> getData_tmp.matches("(?i)^=" + func + "\\(.*"))) {
                        table[x][y].setType(Ex2Utils.ERR_FUNC);
                        table[x][y].setOrder(Ex2Utils.ERR_FUNC);
                    }

                    // case of formula cycle
                    else {
                        table[x][y].setType(Ex2Utils.ERR_CYCLE_FORM);
                        table[x][y].setOrder(Ex2Utils.ERR_CYCLE_FORM);
                    }
                }
            }
        }

        // return depth array we just finished calculating:
        return ans;
    }

    /**
     * computes the value of the cell in the x,y coordinate.
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return the string that will be presented in the x,y cell
     */
    @Override
    public String eval(int x, int y) {
        // If the requested coordinates are not in the spreadsheet - we will return null:
        if (!isIn(x, y)) return null;

        // If the cell is empty - then we return null
        if(get(x,y) == null) return null;

        // In the case of a cell with a cycle error in the formula calculation - we will return the relevant error.
        if (get(x,y).getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return Ex2Utils.ERR_CYCLE;
        }

        // let's take the data of the cell:
        String ans = get(x,y).toString();

        // If the cell data is null then we return null:
        if (ans == null) return null;

        // If the type of data inside the Cell is text, then we don't need to make any process on the data - let's return this text:
        if (get(x,y).getType() == Ex2Utils.TEXT) {
            return ans;
        }

        // If the type of data inside the Cell is number, then we don't need to make any Calculations on the number,
        // just convert it to a double and then return it to a string because that's the format of our method:
        if (get(x,y).getType() == Ex2Utils.NUMBER) {
            return Double.toString(Double.parseDouble(get(x,y).getData()));
        }

        // If the cell type is an IF condition, we will try to calculate it using our condition calculation method.
        // Any error that occurs during the calculation will cause the cell to change type to a condition error.
        else if (get(x,y).getType() == Ex2Utils.IF_TYPE) {
            try {
                return computeIF(ans,x,y);
            } catch (StackOverflowError | Exception e) {
                table[x][y].setType(Ex2Utils.ERR_IF);
                table[x][y].setOrder(Ex2Utils.ERR_IF);
                return Ex2Utils.ERR_IF_str;
            }

        // If the cell type is a function, we will try to calculate it using our function calculation method.
        // Any error that occurs during the calculation will cause the cell to change type to a function error.
        } else if (get(x, y).getType() == Ex2Utils.FUCN_TYPE) {
            try {
                return computeFun(ans,x,y).toString();
            } catch (StackOverflowError | Exception e) {
                table[x][y].setType(Ex2Utils.ERR_FUNC);
                table[x][y].setOrder(Ex2Utils.ERR_FUNC);
                return Ex2Utils.ERR_FUCN_str;
            }
        }

        // If the cell type is a formula, we will try to calculate it using our formula calculation method.
        // We will try to calculate it, and catch in case of errors:
        else if (get(x,y).getType() == Ex2Utils.FORM) {
            try {
                return computeForm(ans, x, y).toString(); // We were able to calculate the form!
            } catch (StackOverflowError e) {
                // We have infinite recursion - so we have a cell with a circularity error, we will mark it accordingly:
                table[x][y].setType(Ex2Utils.ERR_CYCLE_FORM);
                table[x][y].setOrder(Ex2Utils.ERR_CYCLE_FORM);
                return Ex2Utils.ERR_CYCLE;
            } catch (Exception e) {
                // There is an error, so you need to change the type of the cell, and print that there is an error in the cell,
                // This means that an error message will be displayed in the table itself, not the formula itself that was entered into the cell.
                get(x,y).setType(Ex2Utils.ERR_FORM_FORMAT);
                return Ex2Utils.ERR_FORM;
            }
        }


        // if we came here so we got a problem (Cell type isn't defined properly), let's return null:
        return null;
    }

    /**
     * computeForm is a method that Tries to calculate the value of a valid formula, if something fails it throws an error
     * meaning assume we got a valid formula in the string, and throw an error if necessary.
     * @param form a String contains formula RAW data
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return The result of the formula - number (Double)!
     */
    Double computeForm(String form, int x, int y) {
        // empty String isn't valid:
        if ((form == null) || form.isEmpty()) throw new IllegalArgumentException("invalid value");

        // we got to have '=' char at the beginning of the String:
        if (form.charAt(0) != '=') {
            throw new IllegalArgumentException("invalid value");
        } else {
            form = form.substring(1); // Remove the '=' char, if necessary we will put it back (recursion)
            form = form.replaceAll("\\s",""); // We will delete all the space chars in the String.
        }

        // If we reach a valid number, we will return its value
        if (SCell.isNumber(form)) return Double.parseDouble(form);

        // Calculate the length of the string
        int formLength = form.length();

        // In case we have an operator at the beginning of the formula, we will add a zero to prevent errors.
        if (form.charAt(0) == '+' || form.charAt(0) == '-')
        {
            form = "0" + form;
        }

        // Let's see if we have parentheses that enclose the entire formula - In that case, we'll take them off and count what's inside.
        // We use the method we prepared below: BracketEndInd
        if (form.charAt(0) == '(') {
            if (BracketEndInd(form.substring(1)) == formLength - 2)
            {
                return computeForm("=" + form.substring(1, formLength - 1), x,y);
            }
        }

        // Check if it's a cell reference:
        if (form.matches("^[a-zA-Z]{1}\\d{0,3}$"))
        {
            // create an object for the coordinates we are referring to:
            CellEntry ref_cell = new CellEntry(form);
            if (!ref_cell.isValid()) throw new IllegalArgumentException("Invalid cell reference");
            int Cord_ref_x = ref_cell.getX();
            int Cord_ref_y = ref_cell.getY();

            // check if the coordinates we received are in our table at all
            if (!isIn(Cord_ref_x, Cord_ref_y)) throw new IllegalArgumentException("Invalid cell reference");

            // extract the cell to which we are referring:
            Cell referencedCell = get(form);

            // See if the reference is to the cell we are calculating in - there is a circularity error:
            if (x == Cord_ref_x && y == Cord_ref_y) throw new StackOverflowError("ERR_CYCLE_FORM");

            // If the cell is empty - then there is a calculation error in the cell's formula.
            if (referencedCell == null) throw new IllegalArgumentException("Invalid cell reference");

            try {
                // We will try to get the numerical value of the cell we are referring to:
                String value = eval(new CellEntry(form).getX(), new CellEntry(form).getY());

                // If the value we received from the other cell is empty, then there was a calculation error or the cell is empty,
                // therefore we also have a calculation error in our cell formula:
                if (value == null || value.equals(Ex2Utils.ERR_FORM)) throw new IllegalArgumentException("Invalid cell reference");

                // If we were able to get a numeric value, then we will return it.
                return Double.parseDouble(value);
            }
            catch (StackOverflowError e) {
                // We have infinite recursion - so we have a cell with a circularity error, we will mark it accordingly:
                throw new StackOverflowError("ERR_CYCLE_FORM");
            }
        }

        // Find the index of the "main operation" using the indexOfMainOp method
        int mainOpIn = indexOfMainOp(form);
        // If no operators are found - we will throw an error
        if (mainOpIn == -1) throw new IllegalArgumentException("invalid value");

        // We will split our string into two separate strings, according to the operator we found
        String firstPart = form.substring(0, mainOpIn);
        String secondPart = form.substring(mainOpIn + 1);
        char operator = form.charAt(mainOpIn);

        // We will perform the connection between the parts - according to the operator, each part will be calculated in this method again (recursion):
        return switch (operator) {
            case '+' -> (computeForm("=" + firstPart, x, y)) + (computeForm("=" + secondPart, x, y));
            case '-' -> (computeForm("=" + firstPart, x, y)) - (computeForm("=" + secondPart, x, y));
            case '*' -> (computeForm("=" + firstPart, x, y)) * (computeForm("=" + secondPart, x, y));
            case '/' -> (computeForm("=" + firstPart, x, y)) / (computeForm("=" + secondPart, x, y));
            default ->

                // If there is any problem - we will throw an error:
                    throw new IllegalArgumentException("invalid value");
        };

    }

    /**
     * A method that searches for the main arithmetic operator of within a given string
     * @param form a String of a formula
     * @return The index of the main arithmetic operation in the formula
     */
    int indexOfMainOp(String form) {
        // A counter that will help us track parentheses - we will track when parentheses open and close, and thus we will ignore what is inside, so that what is inside the parentheses will be calculated in the following iterations (according to the recursion):
        int bracketCounter = 0;

        // The index of the main operator, -1 as an initial value (will be returned -1 in case we found no operator or an error)
        int mainOpIndex = -1;

        // We will represent each operator with its weight, we will start with a high starting value above all of them. so we can find the operator with the lowest priority ("Main")
        int minPriority = 4;

        // We will loop over the string:
        for (int i = 0; i < form.length(); i++) {
            char c = form.charAt(i);

            // We will ignore what is found inside parentheses as we explained
            if (c == '(') {
                bracketCounter++;
            } else if (c == ')') {
                bracketCounter--;
            } else if (bracketCounter == 0) {
                // We will check the operator's priority order:
                int priority = getOperatorPriority(c);
                if (priority != -1 && priority <= minPriority) {
                    // The main operation is found (until proven else) - we will return the index
                    mainOpIndex = i;
                    minPriority = priority;
                }
            }
        }
        return mainOpIndex;
    }

    /**
     * method that checks the value of the given operator (char)
     * @param operator an char.
     * @return Its precedence as an arithmetic operator
     */
    int getOperatorPriority(char operator) {
        return switch (operator) {
            //first priority is '+' and '-'
            case '+' -> 1;
            case '-' -> 1;

            //second priority is '*' and '/'
            case '*' -> 2;
            case '/' -> 2;

            // in case of a number (not an operator)
            default -> -1;
        };
    }

    /**
     * The function finds the index of the matching closing parenthesis by keeping track of nested parentheses using a counter
     * @param form a String that Removed the '([ character from it, and now you need to find where the parenthesis is ')':
     * @return int an index in String form that Where do the parentheses that were opened at the beginning close.
     */
    int BracketEndInd(String form) {
        // We will use a counter to count how many opening and closing parentheses there are in this string:
        int counter = 0;
        for (int i = 0; i < form.length(); i++) {
            if (form.charAt(i) == '(') counter++;
            if (form.charAt(i) == ')') {
                if (counter == 0) return i;
                counter--;
            }
        }
        // In case of a fault string:
        throw new IllegalArgumentException("invalid value");
    }


    /**
     * this function Calculates the condition, that is, checks whether the condition is true or false, and returns the calculated result - ready to print.
     * @param form a String condition the raw data of the function type cell
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return the value to print according to the condition
     */
    String computeIF (String form, int x, int y) {
        // empty String isn't valid:
        if ((form == null) || form.isEmpty()) throw new IllegalArgumentException("invalid value");

        // For a valid condition string, the string needs to start with "=if(" and end with ")".
        // We will throw an error if the string does not meet these conditions
        // let's use matches in order to cover lowercase and uppercase letters together
        if ((!form.matches("(?i)^=if\\(.*")) || (!form.endsWith(")"))) {
            throw new IllegalArgumentException("Invalid IF format");
        }

        form = form.substring(4,form.length()-1); // Remove the '=if' chars
        form = form.replaceAll("\\s",""); // delete all the space chars in the String

        // Split the string by commas - as the conditional format.
        // we will use regex to ignore commas if they are inside parentheses, so we can support nested conditions
        String[] parts = form.split(",(?![^()]*\\))");
        // If there are not exactly three commas then the condition does not meet the format
        if (parts.length != 3) throw new IllegalArgumentException("Invalid IF format");

        // We will create variables for each part of the condition
        String condition = parts[0].trim();
        String ifTrue = parts[1].trim();
        String ifFalse = parts[2].trim();

        // We will create a CellEntry object to check whether our cell appears within our condition string
        // in that case we have a circular error - we will throw an error.
        CellEntry xyCell = new CellEntry(x, y);
        String toCellName = xyCell.toString();
        if (condition.contains(toCellName) || ifTrue.contains(toCellName) || ifFalse.contains(toCellName)) {
            throw new IllegalArgumentException("Self-referencing IF error");
        }

        // evaluate condition and set the appropriate string result value:
        String SelectedAction;
        if (evaluateCondition(condition, x, y)) {
            SelectedAction = ifTrue;
        } else {
            SelectedAction = ifFalse;
        }

        // We will classify what type of data we received in the result, using the methods of the SCell class.
        SCell result_of_if = new SCell(SelectedAction);

        return switch (result_of_if.getType()) {
            // In the case of a formula, we calculate it using the appropriate method:
            case Ex2Utils.FORM -> Double.toString(computeForm(SelectedAction, x, y));

            // In the case of a number, let's parse the number to Double, then parse it to String:
            case Ex2Utils.NUMBER -> Double.toString(Double.parseDouble(SelectedAction));

            // In the case of a text, let's return the test String as is:
            case Ex2Utils.TEXT -> SelectedAction;

            // In the case of a condition, we calculate it using the appropriate method (nested condition):
            case Ex2Utils.IF_TYPE -> computeIF(SelectedAction, x, y);

            // In the case of a function, we calculate it using the appropriate method:
            case Ex2Utils.FUCN_TYPE -> computeFun(SelectedAction, x, y).toString();

            // In case of undefined content type (including error type) - we will push an error:
            default -> throw new IllegalArgumentException("Invalid IF arguments");
        };
    }

    /**
     * this function Calculates the function, According to the range it receives and according to predefined functions
     * @param form a String contains the raw data of the function type cell
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return the result of the function calculation
     */
    Double computeFun (String form, int x, int y) {
        // empty String isn't valid:
        if ((form == null) || form.isEmpty()) throw new IllegalArgumentException("invalid value");

        // First, we will loop over the various functions that are predefined for us, so we can easily perform our calculations and tests:
        for (int Function = 0; Function < Ex2Utils.FUNCTIONS.length; Function++) {

            // For a valid function string, the string needs to start with "=<FUNCTION>(" and end with ")".
            // We will throw an error if the string does not meet these conditions
            // let's use matches in order to cover lowercase and uppercase letters together
            if ((form.matches("(?i)^=" + Ex2Utils.FUNCTIONS[Function]+ "\\(.*")) && (form.endsWith(")")))
            {
                // We will remove the beginning of the string: "=<FUNCTION>(" and the ")" at the end. so we are left only with the range "Xnn:Ynn" (nn is a number for the Y-cord).
                int selectRMV = Ex2Utils.FUNCTIONS[Function].length()+2;
                form = form.substring(selectRMV,form.length()-1);

                // We will create an object of the range, and insert the range we received in the string (according to the format, this is what should remain in the string as we said)
                Range2D range = new Range2D(form);

                // We will throw an error in the following cases:
                // - If the string is not formatted correctly / invalid range
                // - Our cell is within the range - function cycle error
                // - The range is inside our table (It is enough to check the last index and if it does not exceed then the starting index must also be smaller than it so it does not exceed)
                if (!range.isValidRange() || range.insideRange(x,y) || !isIn(range.getEndX(),range.getEndY())) {
                    throw new IllegalArgumentException("Invalid range");
                }

                // We will convert the range to a list, so we can easily perform function calculations on it:
                List<Double> AllCellRange = getRangeCells(range);

                // The range is correct, so we send it to the appropriate function for calculation:
                switch (Function) {
                    case 0: return Ex2Utils.sum(AllCellRange);
                    case 1: return Ex2Utils.average(AllCellRange);
                    case 2: return Ex2Utils.min(AllCellRange);
                    case 3: return Ex2Utils.max(AllCellRange);
                }
            }
        }
        // In case we get here there is an error, we will define the cell with a function error and throw an error:
        table[x][y].setType(Ex2Utils.ERR_FUNC);
        throw new IllegalArgumentException("Invalid function format");
    }

    /**
     * This method scans the cells within the defined range, checks if they contain numerical data (A formula, function, condition that returns some numeric value that is not text.),
     * and collects them as a list of Double values.
     * If a cell is empty it is ignored!
     * If a cell contains text which is invalid data -  an error is thrown.
     * @param range A Range2D object: the area to scan for numerical values
     * @return A List of Double containing all numerical values within the specified range.
     */
    private List<Double> getRangeCells (Range2D range) {
        // first let's creat an ArrayList:
        List<Double> AllCellRange = new ArrayList<>();

        // We will go through all the cells in our range, according to our EX2Sheet class's table.
        for (int i = range.getStartX(); i <= range.getEndX(); i++) { // X-cord
            for (int j = range.getStartY(); j <= range.getEndY(); j++) { // Y-cord
                if (isIn(i,j)) // Making sure we are within range, to avoid an array out of index error
                {
                    // We will convert the content we have in this cell
                    // as long as it is not empty, because an empty cell is defined as text, but we will simply ignore them as we defined
                    try {
                        if ((table[i][j].getData() != null) && (!Objects.equals(table[i][j].getData(), ""))) {
                            AllCellRange.add(Double.parseDouble(value(i, j)));
                        }
                    }
                    // If we had an error converting to a numeric value of type Double - there is a text cell - we will throw an error:
                    catch (Exception e) {
                        throw new IllegalArgumentException("Invalid range - computable (numerical) value only");
                    }
                }
            }
        }

        // We will return the list with all our numeric values:
        return AllCellRange;
    }

    /**
     * This method parses a conditional expression (e.g., "A1*2 > B2") and evaluates it as true or false.
     * It supports the following operators: <, >, ==, <=, >=, !=.
     * The operands in the condition can be either numerical values (formula or number).
     * If the condition format is invalid, or if it references a non-computable value, an error is thrown.
     * @param condition A String representing the condition to be evaluated (e.g. "A1 > B2").
     * @param x integer, x-coordinate of the cell.
     * @param y integer, y-coordinate of the cell.
     * @return true if the condition is "satisfied", false otherwise.
     */
    private boolean evaluateCondition(String condition, int x, int y) {
        Integer selectedOP = null; // the operator of the condition, according to Ex2Utils.B_OPS array

        // Searches for the operator within the string:
        for (int i = 0; i < Ex2Utils.B_OPS.length; i++) {
            if (condition.contains(Ex2Utils.B_OPS[i])) {
                // We found an operator, let's save it and break out of the loop
                selectedOP = i;
                break;
            }
        }

        // We will throw an error if we do not find an operator.
        if (selectedOP == null) throw new IllegalArgumentException("Invalid IF format");

        // We will split the string according to the operator
        String[] ConditionParts = condition.split(Ex2Utils.B_OPS[selectedOP]);

        // In case we did not get exactly two parts from the string division by the operator - we will throw an error.
        if (ConditionParts.length != 2) throw new IllegalArgumentException("Invalid IF format");

        // Numeric variables for both parts of the condition
        double val1,val2;

        // We will try to calculate the numerical value of both parts of the condition
        // - in case of an error in the calculation we will throw an error
        try {
            val1 = computeForm("=" + ConditionParts[0], x, y);
            val2 = computeForm("=" + ConditionParts[1], x, y);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid IF format");
        }

        // We will perform the comparison according to the requested operator - and return True or false value accordingly:
        return switch (selectedOP) {
            case 0 -> val1 <= val2;
            case 1 -> val1 >= val2;
            case 2 -> val1 < val2;
            case 3 -> val1 > val2;
            case 4 -> val1 == val2;
            case 5 -> val1 != val2;
            default -> throw new IllegalArgumentException("Invalid IF arguments"); // Operator selection error
        };
    }

    /**
     * Load the content of a saved SpreadSheet into this SpreadSheet.
     * all the old cells in before the load operation will be cleared.
     * file format is, when n is the line index n > 0:
     * line 0 : header line
     * line n : <x>,<y>, The cell String, remarks (not to be parsed).
     * We will use BufferedReader so we can get a line for each cell.
     * @param fileName a String representing the full (an absolute or relative path to the loaded file).
     * @throws IOException an exception might be throed if the file can not be loaded.
     */
    @Override
    public void load(String fileName) throws IOException {
        // Create a BufferedReader to read lines from the file (try-catch in case of error)
        try (BufferedReader loader = new BufferedReader(new FileReader(fileName))) {
            String line; // the Cell raw data.
            int lineCount = 0; // line counter.

            // Reset all cells to empty cell:
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
                }
            }

            // Read the file line by line
            while ((line = loader.readLine()) != null) {
                lineCount++; // Starting the line count form 1: first line (header) is 1.

                // Skip header line
                if (lineCount == 1) {
                    continue;
                }

                // Split only for the first three parts (x, y, content) - This way we can ignore any comma that appears in the cell content itself (for example in a conditional cell).
                String[] parts = line.split(",", 3);

                // Skip invalid lines
                if (parts.length < 3) {
                    continue;
                }

                try {
                    // Parse x and y coordinates, trimming any whitespace
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());

                    // Extract cell content and clean it
                    String content = parts[2].trim();

                    // if X and Y coordinates are valid
                    if (isIn(x, y)) {
                        // Set the cell content with the file data
                        table[x][y] = new SCell(content);
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid number format or readline errors.
                    continue;
                }
            }
            // Closing the reader
            loader.close();

            // Recalculate all cells after loading
            eval();
        }
    }

    /**
     * Saves this SpreadSheet into a text file.
     * Only none empty cells should be saved.
     * file format is, when n is the line index n > 0:
     * line 0 : header line
     * line n : <x>,<y>, The cell String, remarks (not to be parsed).
     * We will use BufferedReader so we can get a line for each cell.
     * @param fileName a String representing the full (an absolute or relative path tp the saved file).
     * @throws IOException an exception might be throed if the file can not be saved.
     */
    @Override
    public void save(String fileName) throws IOException {
        // Create a BufferedWriter to write lines to the file (try-catch in case of error)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the header line to the file
            writer.write("I2CS ArielU: SpreadSheet (Ex4) assignment");
            writer.newLine();

            // Iterate through all cells in the SpreadSheet
            for (int x = 0; x < width(); x++) { // X - loop
                for (int y = 0; y < height(); y++) { // Y - loop
                    Cell cell = table[x][y];

                    // Check if the cell is not empty
                    if (cell != null && !cell.toString().isEmpty()) {
                        // Write the cell coordinates and content to the file
                        writer.write(x + "," + y + "," + cell.getData());

                        // release to the next line
                        writer.newLine();
                    }
                }
            }
        }
    }
}