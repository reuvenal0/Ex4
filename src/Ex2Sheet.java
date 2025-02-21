import java.io.*;
import java.util.*;

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
                    if (c.getOrder() == Ex2Utils.ERR_CYCLE_FORM) {
                        // If there is a circularity error in the formula inside this Cell then we will print the prefix error String:
                        c.setType(Ex2Utils.ERR_CYCLE_FORM);
                        ans = Ex2Utils.ERR_CYCLE; return ans;
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
        // Calculate the depths (dependency) of all the cells in our spreadsheet:
        int[][] depths = depth();

        // Find maximum depth for non-cyclic cells
        int maxDepth = 0;
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (depths[i][j] > maxDepth) {
                    maxDepth = depths[i][j];
                }
            }
        }

        // Reset all formula cells to their original type - This way we can update in case there are now calculation errors:
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                Cell cell = get(i, j);
                if (cell != null && cell.getData() != null && !cell.getData().isEmpty()) {
                    // ??
                    if (cell.getData().startsWith("=if")) {
                        cell.setType(Ex2Utils.IF_TYPE);
                    }

                    if (Arrays.stream(Ex2Utils.FUNCTIONS).anyMatch(func -> cell.getData().startsWith("=" + func))) {
                        cell.setType(Ex2Utils.FUCN_TYPE);
                    }

                    // If it starts with '=', it's a formula (until proven otherwise)
                    if (cell.getData().startsWith("=") &&
                            (!cell.getData().startsWith("=if")) &&
                            (Arrays.stream(Ex2Utils.FUNCTIONS).noneMatch(func -> cell.getData().startsWith("=" + func))) )
                    {
                        cell.setType(Ex2Utils.FORM);
                    }
                }
            }
        }

        // Evaluate cells level by level  based on their depth,
        // We will use three loops for this:
        for (int currentDepth = 0; currentDepth <= maxDepth; currentDepth++) // depth loop
        {
            for (int i = 0; i < width(); i++) // X-cord (width) loop
            {
                for (int j = 0; j < height(); j++) // Y-cord (height) loop
                {
                    if (depths[i][j] == currentDepth)
                    {
                        // extract the cell's raw data:
                        Cell cell = get(i, j);
                        if (cell != null)
                        {
                            if (depths[i][j] == Ex2Utils.ERR_CYCLE_FORM) // The depth of the cell is defined in the depth array as a circularity error -
                            {
                                if (table[i][j].getType() == Ex2Utils.IF_TYPE || table[i][j].getType() == Ex2Utils.ERR_IF) {
                                    table[i][j].setType(Ex2Utils.ERR_IF);
                                    table[i][j].setOrder(Ex2Utils.ERR_IF);
                                }
                                else if (table[i][j].getType() == Ex2Utils.FUCN_TYPE) {
                                    table[i][j].setType(Ex2Utils.ERR_FUNC);
                                    table[i][j].setOrder(Ex2Utils.ERR_FUNC);
                                }
                                else {
                                    //  we have a cell with a circularity error, we will mark it accordingly:
                                    cell.setOrder(Ex2Utils.ERR_CYCLE_FORM);
                                    cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                                }
                            }
                            else {
                                String calculated = eval(i, j);
                                cell.setOrder(currentDepth); // Set the Cell order
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
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = get(x, y);

                    // Skip if cell is null or Text or Number - set depth as 0:
                    if (cell == null || cell.getType() == Ex2Utils.NUMBER || cell.getType() == Ex2Utils.TEXT) {
                        if (ans[x][y] == -1)
                        {
                            ans[x][y] = 0; // A cell of these types must have a depth of 0

                            // We calculated a cell
                            count++;
                            flagC = true;
                        }
                        continue;
                    }

                    // If this cell is still unprocessed (-1)
                    if (ans[x][y] == -1)
                    {
                        // Extract the formula
                        String formula = cell.getData().substring(1);
                        boolean canProcess = true;
                        int maxDepth = 0; // Track the maximum depth of dependencies

                        // Parse the formula to find all referenced cells
                        for (int i = 0; i < formula.length(); i++) {
                            if (Character.isLetter(formula.charAt(i))) {
                                int endIndex = i + 1;
                                // Go over the formula (string) - finding the reference:
                                while (endIndex < formula.length() && Character.isDigit(formula.charAt(endIndex))) {
                                    endIndex++;
                                }

                                // We will extract the reference within the formula:
                                String ref = formula.substring(i, endIndex);
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

                                i = endIndex - 1;
                            }
                        }

                        // If all dependencies are processed, calculate the cell's depth
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
                break;
            }
        }
        // We will make sure that all the rounded cells are marked: that the value in our array remains -1, meaning that we were unable to calculate it.
        // Changing their type to ERR_CYCLE_FORM:
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (ans[i][j] == -1) {
                    if (table[i][j].getData().startsWith("=if")) {
                        table[i][j].setType(Ex2Utils.ERR_IF);
                        table[i][j].setOrder(Ex2Utils.ERR_IF);
                    } else {
                        table[i][j].setType(Ex2Utils.ERR_CYCLE_FORM);
                        table[i][j].setOrder(Ex2Utils.ERR_CYCLE_FORM);
                    }
                }
            }
        }
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

        if (get(x,y).getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return Ex2Utils.ERR_CYCLE;
        }

        // We will take the data of the cell:
        String ans = get(x,y).toString();

        // If the cell data is null then we return null:
        if (ans == null) return null;

        // If the type of data inside the Cell is text, then we don't need to make any process on the data - let's return this text:
        if (get(x,y).getType() == Ex2Utils.TEXT) {
            return ans;
        }

        // If the type of data inside the Cell is number, then we don't need to make any Calculations on the number:
        // We'll just convert it to a double and then return it to a string because that's the format of the method
        if (get(x,y).getType() == Ex2Utils.NUMBER) {
            return Double.toString(Double.parseDouble(get(x,y).getData()));
        }
        // ?? אולי יש לנו כאן תנאי - נחשב אותו ונחזיר שגיאת תנאי בכל מצב שמשהו משתבש
        else if (get(x,y).getType() == Ex2Utils.IF_TYPE) {
            try {
                return computeIF(ans,x,y);
            } catch (StackOverflowError | Exception e) {
                table[x][y].setType(Ex2Utils.ERR_IF);
                table[x][y].setOrder(Ex2Utils.ERR_IF);
                return Ex2Utils.ERR_IF_str;
            }
        } else if (get(x, y).getType() == Ex2Utils.FUCN_TYPE) {
            try {
                return computeFun(ans,x,y).toString();
            } catch (StackOverflowError | Exception e) {
                table[x][y].setType(Ex2Utils.ERR_FUNC);
                table[x][y].setOrder(Ex2Utils.ERR_FUNC);
                return Ex2Utils.ERR_FUCN_str;
            }
        }

        // If the content is neither text nor a number, then there is a formula that needs to be calculated
        // We will try to calculate it, and catch in case of an error:
        else if (get(x,y).getType() == Ex2Utils.FORM) {
            try {
                return computeForm(ans, x, y).toString();
                // We were able to calculate the form!
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
        // if we came here so we got a problem, let's return null:
        return null;
    }

    /**
     * computeForm is a method that Tries to calculate the value of a valid formula, if something fails it throws an error
     * meaning assume we got a valid formula in the string, and throw an error if necessary.
     * @param form a String contains formula
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
        switch (operator) {
            case '+':
                return (computeForm("=" + firstPart, x,y)) + (computeForm("=" + secondPart, x,y));
            case '-':
                return (computeForm("=" + firstPart, x,y)) - (computeForm("=" + secondPart, x,y));
            case '*':
                return (computeForm("=" + firstPart, x,y)) * (computeForm("=" + secondPart, x,y));
            case '/':
                return (computeForm("=" + firstPart, x,y)) / (computeForm("=" + secondPart, x,y));
        }

        // If there is any problem - we will throw an error:
        throw new IllegalArgumentException("invalid value");
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
        switch (operator) {
            //first priority is '+' and '-'
            case '+':
                return 1;
            case '-':
                return 1;

            //second priority is '*' and '/'
            case '*':
                return 2;
            case '/':
                return 2;

            // in case of a number (not an operator)
            default:
                return -1;
        }
    }

    //
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


    String computeIF (String form, int x, int y) {
        // Handle 'If' function - Ex4 addition:

        // empty String isn't valid:
        if ((form == null) || form.isEmpty()) throw new IllegalArgumentException("invalid value");

        // we got to have '=' char at the beginning of the String:
        // we can write 'if' in upper or lower case
        // if must end with ')'
        if ((!form.startsWith("=if(")) || (!form.endsWith(")"))) {
            throw new IllegalArgumentException("Invalid IF format");
        }

        form = form.substring(4,form.length()-1); // Remove the '=' char, if necessary we will put it back (recursion)
        form = form.replaceAll("\\s",""); // We will delete all the space chars in the String.

        String[] parts = form.split(",");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid IF format");

        // Extract condition and branches
        String condition = parts[0].trim();
        String ifTrue = parts[1].trim();
        String ifFalse = parts[2].trim();

        CellEntry xyCell = new CellEntry(x, y);
        String toCellName = xyCell.toString();

        if (condition.contains(toCellName) || ifTrue.contains(toCellName) || ifFalse.contains(toCellName)) {
            throw new IllegalArgumentException("Self-referencing IF error");
        }

        table[x][y].setType(Ex2Utils.IF_TYPE);

        // Evaluate condition and return appropriate value
        String SelectedAction;
        if (evaluateCondition(condition, x, y)) {
            SelectedAction = ifTrue;
        } else {
            SelectedAction = ifFalse;
        }

        SCell result_of_if = new SCell(SelectedAction);
        if (result_of_if.getType() == Ex2Utils.FORM) return Double.toString(computeForm(SelectedAction, x, y));
        else if (result_of_if.getType() == Ex2Utils.NUMBER) return Double.toString(Double.parseDouble(SelectedAction));
        else if (result_of_if.getType() == Ex2Utils.TEXT) return SelectedAction;
        else throw new IllegalArgumentException("Invalid IF format");
    }

    Double computeFun (String form, int x, int y) {
        // empty String isn't valid:
        if ((form == null) || form.isEmpty()) throw new IllegalArgumentException("invalid value");

        // we got to have '=' char at the beginning of the String:
        // we can write 'if' in upper or lower case
        // if must end with ')'

        for (int i = 0; i < Ex2Utils.FUNCTIONS.length; i++) {
            if ((form.startsWith("=" + Ex2Utils.FUNCTIONS[i]) && (form.endsWith(")"))))
            {
                int selectRMV = Ex2Utils.FUNCTIONS[i].length()+2;
                form = form.substring(selectRMV,form.length()-1);

                Range2D range = new Range2D(form);
                if (!range.isValidRange() || range.insideRange(x,y) || !isIn(range.getEndIndex().getX(),range.getEndIndex().getX())) throw new IllegalArgumentException("Invalid range");
                table[x][y].setType(Ex2Utils.FUCN_TYPE);
                List<Double> AllCellRange = getRangeCells(range);

                switch (i) {
                    case 0: return sum(AllCellRange);
                    case 1: return average(AllCellRange);
                    case 2: return min(AllCellRange);
                    case 3: return max(AllCellRange);
                }
            }
        }
        // ?? todo אם הגענו עד לפה אז אין מה לחפש כאן
        table[x][y].setType(Ex2Utils.ERR_FUNC);
        throw new IllegalArgumentException("Invalid function format");
    }

    private List<Double> getRangeCells (Range2D range) {
        List<Double> AllCellRange = new ArrayList<>();
        for (int i = range.getStartX(); i <= range.getEndX(); i++) {
            for (int j = range.getStartY(); j <= range.getEndY(); j++) {
                if (isIn(i,j))
                {
                    System.out.println("x: " + i);
                    System.out.println("y: " + j);

                    try {
                        if ((table[i][j].getData() != null) && (!Objects.equals(table[i][j].getData(), ""))) {
                            AllCellRange.add(Double.parseDouble(value(i, j)));
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid range - computable (numerical) value only");
                    }
                }
            }
        }
        return AllCellRange;
    }

    private double sum (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0;
        double sum = 0;
        for (Double CellVal : AllCellRange) {
            sum += CellVal;
        }
        return sum;
    }

    private double average (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0;
        double sum = 0;
        for (Double CellVal : AllCellRange) {
            sum += CellVal;
        }
        return sum/AllCellRange.size();
    }

    private double min (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0;
        return Collections.min(AllCellRange);
    }

    private double max (List<Double> AllCellRange) {
        if (AllCellRange == null || AllCellRange.isEmpty()) return 0.0;
        return Collections.max(AllCellRange);
    }

    /**
     *
     * @param condition
     * @param x
     * @param y
     * @return
     */
    private boolean evaluateCondition(String condition, int x, int y) {
        String selectedOp = null;
        for (String op : Ex2Utils.B_OPS) {
            if (condition.contains(op)) {
                selectedOp = op;
                break;
            }
        }
        if (selectedOp == null) throw new IllegalArgumentException("Invalid IF format");

        String[] ConditionParts = condition.split(selectedOp);
        if (ConditionParts.length != 2) throw new IllegalArgumentException("Invalid IF format");
        double val1,val2;
        try {
            val1 = computeForm("=" + ConditionParts[0], x, y);
            val2 = computeForm("=" + ConditionParts[1], x, y);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid IF format");
        }

        switch (selectedOp) {
            case "<": return val1 < val2;
            case ">": return val1 > val2;
            case "==": return val1 == val2;
            case "<=": return val1 <= val2;
            case ">=": return val1 >= val2;
            case "!=": return val1 != val2;
            default: throw new IllegalArgumentException("Invalid IF arguments");
        }
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
        try (BufferedReader loader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineCount = 0;

            // Reset all cells to empty
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    table[i][j] = new SCell("");
                }
            }

            while ((line = loader.readLine()) != null) {
                lineCount++;

                // Skip header line
                if (lineCount == 1) {
                    continue;
                }

                // Split only for the first three parts (x, y, content)
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
                    String content = parts[2];

                    // If content contains a comma, take only the part before the first comma after position 2
                    int commaIndex = content.indexOf(',');
                    if (commaIndex != -1) {
                        content = content.substring(0, commaIndex);
                    }

                    // delete extra spaces
                    content = content.trim();

                    // Set cell content if coordinates are valid
                    if (isIn(x, y)) {
                        table[x][y] = new SCell(content);
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid number format
                    continue;
                }
            }
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the header line
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment");
            writer.newLine();

            // loop through the SpreadSheet:
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    Cell cell = table[i][j];
                    if (cell != null && !cell.toString().isEmpty()) {
                        // Writing the Cell Cord and data:
                        writer.write(i + "," + j + "," + cell.getData());
                        // release to the next line
                        writer.newLine();
                    }
                }
            }
            writer.close();
        }
    }
}