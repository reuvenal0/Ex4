/**
 * Represents a 2D cell index for spreadsheet coordinates.
 * Supports two representation formats:
 * 1. Integer coordinates (X, Y):
 *    - X: 0 to 25 (corresponding to A-Z)
 *    - Y: 0 to 99
 * 2. String index format: 'Letter + 2 digits' (e.g., 'A0', 'Z67').
 * Implements Index2D interface.
 */
public class CellEntry  implements Index2D {
    private int x, y; // 1. X and Y coordinates representing the cell index.
    private String cords; // 2. String representation of the cell index (e.g., "A0", "Z67").

    /**
     * constructor for an CellEntry object -
     * converts form String coordinates (index) to x,y integer value
     * If invalid or null, the index is marked as an error.
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
            //Set the Integers as ERR
            x = y = Ex2Utils.ERR;
        }
        // Valid string format - proceed to parse:
        else {
            this.cords = cords.toUpperCase();
            parseEntry(); // Convert the string to integer coordinates.
        }
    }

    /**
     * constructor for an CellEntry object -
     * converts form x and Y coordinates (Integers) to String coordinates
     * If the coordinates are valid, the string index is constructed using Ex2Utils.ABC array.
     * If invalid, the index is marked as an error.
     * @param xx The x-coordinate (0 to 25, corresponding to letters A to Z).
     * @param yy The y-coordinate (0 to 99).
     */
    public CellEntry(int xx, int yy) {
        // Check if the coordinates are within the valid range:
        if (yy >= 0 && yy <= 99 && xx >= 0 && xx <= 25)
        {
            // valid coordination: let's use the Ex2Utils ABC array, and construct CellEntry object:
            String cordsXY = Ex2Utils.ABC[xx] + Integer.toString(yy);
            this.cords = cordsXY.toUpperCase().trim();
            // Parse the newly constructed string to initialize x and y:
            parseEntry();
        } // Invalid coordinates - mark as error:
        else {
            this.cords = Ex2Utils.EMPTY_CELL;
            x = y = Ex2Utils.ERR;
        }
    }

    /**
     * Parses the string index (this.cords) to compute the integer coordinates (x and y).
     * - The first character is interpreted as the X index (A=0, B=1, ..., Z=25).
     * - The remaining part is parsed as the Y index (0 to 99).
     * If the string is not in a valid format or if parsing fails, the coordinates are marked as errors.
     */
    private void parseEntry() {
        // Extract the first character and convert it to uppercase (for consistency):
        char c = Character.toUpperCase(cords.charAt(0));
        // A letter less than A and greater than Z is invalid:
        if (c < 'A' || c > 'Z')
        {
            // Invalid character - mark x and y as errors:
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
                // Invalid Y value
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
