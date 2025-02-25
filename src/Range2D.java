public class Range2D {
    /**
     * Range2D represents a 2-dimensional range defined by a start and end cell.
     * It validates the range and provides methods to access the indices and check containment.
     */
    private CellEntry StartIndex = null; // Starting cell of the range
    private CellEntry EndIndex = null; // Ending cell of the range
    private boolean ValidRange = false; // Flag indicating if the range is valid

    /**
     * @return true if the range is valid, false otherwise.
     */
    public boolean isValidRange() {
        return ValidRange;
    }

    /**
     * Gets the starting index of the range.
     * @return StartIndex if the range is valid.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public CellEntry getStartIndex() {
        if (ValidRange) {
            return StartIndex;
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    /**
     * Gets the ending index of the range.
     * @return EndIndex if the range is valid.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public CellEntry getEndIndex() {
        if (ValidRange) {
            return EndIndex;
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    /**
     * Gets the X-coordinate of the starting index.
     * @return X-coordinate of StartIndex.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public int getStartX() {
        if (ValidRange) {
            return this.StartIndex.getX();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    /**
     * Gets the X-coordinate of the ending index.
     * @return X-coordinate of EndIndex.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public int getEndX() {
        if (ValidRange) {
            return this.EndIndex.getX();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }


    /**
     * Gets the Y-coordinate of the starting index.
     * @return Y-coordinate of StartIndex.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public int getStartY() {
        if (ValidRange) {
            return this.StartIndex.getY();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    /**
     * Gets the Y-coordinate of the ending index.
     * @return Y-coordinate of EndIndex.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public int getEndY() {
        if (ValidRange) {
            return this.EndIndex.getY();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    /**
     * Constructs a Range2D object from a string representation.
     * The format should be "start:end" (e.g., "A1:B2").
     * @param range The range string to parse.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public Range2D(String range) {
        if (range == null || range.isEmpty())  //empty String is invalid
        {
            // Invalidate the range if checks fail
            this.ValidRange = false;
            // Throw exception for an invalid range
            throw new IllegalArgumentException("Invalid range");
        }

        // Split the range string by ":" to separate start and end indices
        String[] parts = range.split(":");

        // Check if exactly two parts are present (start and end indices)
        if (parts.length == 2) {
            // Create CellEntry objects for start and end indices
            this.StartIndex = new CellEntry(parts[0]);
            this.EndIndex = new CellEntry(parts[1]);

            // Validate indices and ensure start is before or equal to end
            if (StartIndex.isValid() && EndIndex.isValid() && (StartIndex.getX() <= EndIndex.getX()) && StartIndex.getY() <= EndIndex.getY()) {
                // Set the range as valid if all checks pass
                this.ValidRange = true;
            } else {
                // Invalidate the range if checks fail
                this.ValidRange = false;
                this.StartIndex = null;
                this.EndIndex = null;
                // Throw exception for an invalid range
                throw new IllegalArgumentException("Invalid range");
            }
        }
    }

    /**
     * Checks if a given point (x, y) is inside the range.
     * @param x X-coordinate to check.
     * @param y Y-coordinate to check.
     * @return true if the point is inside the range, false otherwise.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public boolean insideRange (int x, int y) {
        if (this.ValidRange) {
            return (((getStartX() <= x && x <= getEndX())) && ((getStartY() <= y) && (y <= getEndY())));
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }
}