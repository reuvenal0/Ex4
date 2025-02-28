import java.util.Arrays;

/**
 * SCell - A class representing a single spreadsheet cell.
 *
 * - Stores raw data (as String) and determines the type of data.
 * - Supports data types: TEXT, NUMBER, FORM, IF_TYPE, FUCN_TYPE.
 * - Type is calculated using patterns and rules defined in Ex2Utils.
 * - Includes utility methods for type checks and data manipulation.
 *
 * This class is used by Ex2Sheet for managing individual cell behavior.
 */
public class SCell implements Cell {
    private String line; // The String data saves in the Cell (raw data - Not calculated)
    private int type; // The type of cell - according to the settings in Ex2Utils
    private int order; // representing the natural order of this Cell - calculated in Ex2Sheet class

    /**
     * constructor of a cell object :
     * Inserts the information we received into the Cell data String,
     * and calculates the type of raw data found in the cell.
     * @param s a String with the raw data of the Cell.
     */
    public SCell(String s) {
        setData(s);  // Sets the raw data and computes the type
        computeType();  // Determines the type of data (e.g., NUMBER, TEXT)
    }

    /**
     * Computes the type of data stored in the cell.
     * - Checks the raw data and assigns a type (e.g., TEXT, NUMBER, FORM, FUNCTION, CONDITION).
     * - Type is stored in the int 'type' field -  according to the settings in Ex2Utils.
     *
     * Type determination order:
     *  1. Empty string or null -> TEXT (empty!)
     *  2. Valid (double) number -> NUMBER
     *  3. Conditional (starts with "=if(") -> IF_TYPE
     *  4. Function (matches "=<Ex2Utils.FUNCTIONS>(") -> FUCN_TYPE
     *  5. Formula (starts with "=" but not IF or FUNCTION) -> FORM
     *  6. Otherwise -> TEXT
     *
     *  The method does not verify that the value in the cell is correct!
     * It only roughly checks where to assign the cell to perform the calculation.
     * During the calculation, it checks whether the value in the cell is correct,
     * and then if there is an error, it changes the type to the appropriate error.
     */
    private void computeType() {
        // An empty string is considered text (unless proven otherwise).
        if (line == null || line.isEmpty())
        {
            type = Ex2Utils.TEXT;
            return;
        }
        // Check if the data is a valid number:
        if (isNumber(line)) {
            // The cell contains a valid number (Double) if we were able to convert it to a number properly
            // (using a function we created that catches the conversion error if there is an error)
            type = Ex2Utils.NUMBER;
        }

        // Check for IF condition:
        else if (line.matches("(?i)^=if\\(.*")) {
            type = Ex2Utils.IF_TYPE;
        }

        // Check for function (e.g., SUM, AVERAGE):
        else if (Arrays.stream(Ex2Utils.FUNCTIONS).anyMatch(func -> line.matches("(?i)^=" + func + "\\(.*"))) {
            type = Ex2Utils.FUCN_TYPE;
        }

        // We have a formula if the first character is just an '='
        // the test if there is any error in the formula is considered in the spreadsheet class
        else if ((line.charAt(0) == '=') && (!line.startsWith("=if"))
                && (Arrays.stream(Ex2Utils.FUNCTIONS).noneMatch(func -> line.matches("(?i)^=" + func + "\\(.*"))))
        {
            type = Ex2Utils.FORM;
        }

        // otherwise, the string is considered text (unless proven otherwise).
        else {
            type = Ex2Utils.TEXT;
        }
    }

    /**
     * A function that tries to see if the cell's string can be converted to a number (double)
     * @param str a String with the raw data of the Cell
     * @return True if the string can be converted to a number (double) properly
     */
    public static boolean isNumber(String str) {
        // An empty string is not considered a valid number.
        if (str == null || str.isEmpty()) return false;

        try {
            Double.parseDouble(str);
            // We got here so the conversion is correct - therefore there is a correct number here:
            return true;
        } catch (NumberFormatException e) {
            // There is a problem - Not a valid number:
            return false;
        }
    }

    /**
     * Return the input data (aka the raw String) this cell is containing (without any computation).
     * @return String line (raw cell data)
     * */
    @Override
    public String toString() {
        return getData();
    }

    /**
     * Return the input data (aka the raw String) this cell is containing (without any computation).
     * @return String line (raw cell data)
     * */
    @Override
    public String getData() {
        return line;
    }

    /**
     * Allows you to redefine the data of an existing cell,
     * and from there we will check what type of new data was entered.
     * @param s a String with the new data we want to put in this existing cell
     */
    @Override
    public void setData(String s) {
        this.line = s;
        computeType();
    }

    /**
     * Returns the type of this cell according to the settings in Ex2Utils.
     * @return an int value of the type of the Cell (e.g., TEXT, NUMBER, FORM, FUNCTION, CONDITION)
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Changes the type of this Cell according to the settings in Ex2Utils.
     * - Manually sets the type of the cell.
     * - Used for error handling or special cases.
     * @param t an int value of to set the type of the Cell (e.g., TEXT, NUMBER, FORM, FUNCTION, CONDITION)
     */
    @Override
    public void setType(int t) {
        type = t;
    }

    /**
     * - Used in Ex2Sheet to determine evaluation order.
     * @return an integer representing the "number of rounds" needed to compute this cell - calculated in Ex2Sheet class.
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Changes the order of this Cell
     * - Used for dependency depth in Ex2Sheet.
     * @param t an Integer num - representing the natural order of this Cell - calculated in Ex2Sheet class.
     */
    @Override
    public void setOrder(int t) {
        this.order = t;
    }
}