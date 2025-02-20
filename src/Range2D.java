public class Range2D {
    private CellEntry StartIndex = null;
    private CellEntry EndIndex = null;
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
}
