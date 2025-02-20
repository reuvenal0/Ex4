public class Range2D {
    private CellEntry StartIndex = null;
    private CellEntry EndIndex = null;

    public boolean isValidRange() {
        return ValidRange;
    }

    private boolean ValidRange;

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

    public Range2D(String range) {
        if (range == null || range.length() == 0) {
            this.ValidRange = false;
            return;
        }

        String[] parts = range.split(":");

        if (parts.length != 2 && StartIndex.isValid() && EndIndex.isValid()) {
            this.StartIndex = new CellEntry(parts[0]);
            this.EndIndex = new CellEntry(parts[1]);
            this.ValidRange = true;
        } else {
            this.ValidRange = false;
        }
    }

    public boolean insideRange (int x, int y) {
        if (((StartIndex.getX() <= x && x <= EndIndex.getX())) && ((StartIndex.getY() <= y) && (y <= EndIndex.getY()))) return true;
        return false;
    }
}
