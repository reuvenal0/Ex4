public class CellEntry  implements Index2D {
    // This class represents a simple 2D cell index (as in a spreadsheet),In two equivalent ways of representation:
    // X, Y coordinates (Integers): X form 0 to 26, Z form 0 to 99.
    // index 'char + 2 digit': for exemple: 'A0' or 'Z67'.

    private int x, y; // First representation method
    private String cords; // Second representation method

    /**
     * constructor for an CellEntry object -
     * converts form String coordinates (index) to x,y integer value
     * @param cords an index in the spreadsheet: 'char + 2 digit': for exemple: 'A0' or 'Z67'.
     */
    public CellEntry(String cords) {
        // empty or too small String, is invalid:
        if (cords == null) {
            this.cords = Ex2Utils.EMPTY_CELL;
            //Set the Integers as ERR
            x = y = Ex2Utils.ERR;
        } else if (cords.length() < 2) {
            this.cords = cords;
            x = y = Ex2Utils.ERR;
        } else { // valid String
            this.cords = cords.toUpperCase();
            parseEntry(); // coverts to Int
        }
    }

    /**
     * constructor for an CellEntry object -
     * converts form x and Y coordinates (Integers) to String coordinates
     * @param xx the x value of required cell coordinate.
     * @param yy the x value of required cell coordinate.
     */
    public CellEntry(int xx, int yy) {
        if (yy >= 0 && yy <= 99 && xx >= 0 && xx <= 25)
        {
            // valid coordination: let's use the Ex2Utils ABC array, and construct CellEntry object:
            String cordsXY = Ex2Utils.ABC[xx] + Integer.toString(yy);
            this.cords = cordsXY.toUpperCase().trim();
            parseEntry();
        } else { //invalid X-Y coordination
            this.cords = Ex2Utils.EMPTY_CELL;
            x = y = Ex2Utils.ERR;
        }
    }

    /**
     * converts form x and Y coordinates (Integers) to String coordinates:
     * using this.cord String, and compute the value for int x and int Y.
     */
    private void parseEntry() {
        // Getting the first char in the String: the letter Index (For the convenience of checking the size - we will convert it to a capital letter)
        char c = Character.toUpperCase(cords.charAt(0));
        // A letter less than A and greater than Z is invalid:
        if (c < 'A' || c > 'Z')
        {
            //Set the Integers as ERR
            x = y = Ex2Utils.ERR;
            return; // End the parsing process
        }

        // Now we will perform the conversion of the values,
        // if there is an error on the conversion, then the value that will be for both coordinates is ERR
        try {
            x = c - 'A'; // We will convert the character to Int (reduced by 65 in, ASCII code)
            y = Integer.parseInt(cords.substring(1)); // Conversion of the remaining digits in the string
            //Digits must be greater than 0 and less than 100
            if (y < 0 || y > 99) {

                //Set the Integers as ERR
                x = y = Ex2Utils.ERR;
            }
        } catch (NumberFormatException e) {
            //In case the conversion was not successful - Set the Integers as ERR
            x = y = Ex2Utils.ERR;
        }
    }

    /**
     * Checking whether the value we created in the conversion was valid, and we did not receive an error during the process.
     * We perform the tests on the coordinates during the conversion, which takes place in any case during every creation of an object - so it is enough to check if we received an error there.
     * @return If two the coordinates (X,Y) are different ERR (aka: valid index).
     */
    @Override
    public boolean isValid() {
        return x != Ex2Utils.ERR && y != Ex2Utils.ERR;
    }

    /**
     * Coordinates also have a way of representation using numerical values (int), so we can work with them more conveniently and calculate calculations.
     * @return the x value (integer) of this index
     */
    @Override
    public int getX() {return this.x;}


    /**
     * Coordinates also have a way of representation using numerical values (int), so we can work with them more conveniently and calculate calculations
     * @return the y value (integer) of this index
     */
    @Override
    public int getY() {return this.y;}

    /**
     * We get the coordinates using a string that marks a cell in the spreadsheet
     * @return the cell index representation in form of a spreadsheet String (e.g., "B3").
     */
    @Override
    public String toString() {
        if (!isValid())
        {
            return Ex2Utils.EMPTY_CELL;
        }
        else {
            return this.cords;
        }
    }
}
