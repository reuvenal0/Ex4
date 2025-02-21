public class Range2D {
    private CellEntry StartIndex = null;
    private CellEntry EndIndex = null;
    private boolean ValidRange = false;

    public boolean isValidRange() {
        return ValidRange;
    }

    public CellEntry getStartIndex() {
        if (ValidRange) {
            return StartIndex;
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    public CellEntry getEndIndex() {
        if (ValidRange) {
            return EndIndex;
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    public int getStartX() {
        if (ValidRange) {
            return this.StartIndex.getX();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    public int getEndX() {
        if (ValidRange) {
            return this.EndIndex.getX();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }


    public int getStartY() {
        if (ValidRange) {
            return this.StartIndex.getY();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }


    public int getEndY() {
        if (ValidRange) {
            return this.EndIndex.getY();
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }

    public Range2D(String range) {
        if (range == null || range.isEmpty()) {
            this.ValidRange = false;
            return;
        }

        String[] parts = range.split(":");

        if (parts.length == 2) {
            this.StartIndex = new CellEntry(parts[0]);
            this.EndIndex = new CellEntry(parts[1]);

            if (StartIndex.isValid() && EndIndex.isValid() && (StartIndex.getX() <= EndIndex.getX()) && StartIndex.getY() <= EndIndex.getY()) {
                this.ValidRange = true;
            } else {
                this.ValidRange = false;
                this.StartIndex = null;
                this.EndIndex = null;
                throw new IllegalArgumentException("Invalid range");
            }
        }
    }

    public boolean insideRange (int x, int y) {
        if (this.ValidRange) {
            return (((getStartX() <= x && x <= getEndX())) && ((getStartY() <= y) && (y <= getEndY())));
        }
        else {throw new IllegalArgumentException("Invalid range");}
    }
}