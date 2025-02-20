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
        else {return null;}
    }

    public CellEntry getEndIndex() {
        if (ValidRange) {
            return EndIndex;
        }
        else {return null;}
    }

    public int getStartX() {
        return this.StartIndex.getX();
    }

    public int getEndX() {
        return this.EndIndex.getX();
    }


    public int getStartY() {
        return this.StartIndex.getY();
    }


    public int getEndY() {
        return this.EndIndex.getY();
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

            if (StartIndex.isValid() && EndIndex.isValid()) {
                this.ValidRange = true;
            } else {
                this.StartIndex = null;
                this.EndIndex = null;
            }
        }
    }

    public boolean insideRange (int x, int y) {
        return (((getStartX() <= x && x <= getEndX())) && ((getStartY() <= y) && (y <= getEndY())));
    }
}
