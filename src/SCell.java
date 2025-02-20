import java.util.Arrays;

public class SCell implements Cell {
    private String line; //The String data saves in the Cell (raw data - Not calculated)
    private int type; // The type of cell - according to the settings in Ex2Utils
    private int order; // representing the natural order of this Cell - calculated in Ex2Sheet class

    /**
     * constructor of a cell object :
     * Inserts the information we received into the Cell data String,
     * and calculates the type of data found in the cell.
     * @param s a String with the raw data of the Cell/
     */
    public SCell(String s) {
        setData(s);
        computeType();
    }

    /**
     * Checks what type of data is in the cell
     * The result is stored in the int type - according to the settings in Ex2Utils
     */
    private void computeType() {
        // An empty string is considered text (unless proven otherwise).
        if (line == null || line.isEmpty())
        {
            type = Ex2Utils.TEXT;
            return;
        }


        if (isNumber(line)) {
            // The cell contains a valid number (Double) if we were able to convert it to a number properly
            // (using a function we created that catches the conversion error if there is an error)
            type = Ex2Utils.NUMBER;
        } else if (line.startsWith("=if")) {
            // this cell is a IF type ??
            type = Ex2Utils.IF_TYPE;
        } else if (Arrays.stream(Ex2Utils.FUNCTIONS).anyMatch(func -> line.startsWith("=" + func))) {
            type = Ex2Utils.FUCN_TYPE;
        }

        // We have a formula if the first character is just an '='
        // the test if there is any error in the formula is considered in the spreadsheet class
        else if ((line.charAt(0) == '=') && (!line.startsWith("=if"))
                && (Arrays.stream(Ex2Utils.FUNCTIONS).noneMatch(func -> line.startsWith("=" + func))))
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
            // We got here so the conversion is correct - therefore there is a correct number here
            return true;
        } catch (NumberFormatException e) {
            // There is a problem:
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
     * Returns the type of this cell {TEXT,NUMBER, FORM, ERR_CYCLE_FORM, ERR_WRONG_FORM}
     * @return an int value (as defined in Ex2Utils)
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Changes the type of this Cell {TEXT,NUMBER, FORM, ERR_CYCLE_FORM, ERR_WRONG_FORM}
     * @param t an int type value as defines in Ex2Utils.
     */
    @Override
    public void setType(int t) {
        type = t;
    }

    /**
     * @return an integer representing the "number of rounds" needed to compute this cell - calculated in Ex2Sheet class.
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Changes the order of this Cell
     * @param t an Integer num - representing the natural order of this Cell - calculated in Ex2Sheet class.
     */
    @Override
    public void setOrder(int t) {
        this.order = t;
    }
}