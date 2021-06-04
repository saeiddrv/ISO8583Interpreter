package ir.saeiddrv.iso8583.message;

public class Range {

    public final static Range OF_PRIMARY_BITMAP = new Range(1, 64);
    public final static Range OF_SECONDARY_BITMAP = new Range(65, 128);
    public final static Range OF_TERTIARY_BITMAP = new Range(129, 192);

    private final int start;
    private final int end;

    public static Range of(int start, int end) {
        return new Range(start, end);
    }

    private Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("@Range[start: %s, end: %s]", start, end);
    }
}
