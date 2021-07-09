package ir.saeiddrv.iso8583.message;

/**
 * This class holds two integer values to create a range of the numbers
 *
 * @author Saeid Darvish
 */
public class Range {

    private final int start;
    private final int end;

    private Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /** The Range object for primary bitmap */
    public final static Range OF_PRIMARY_BITMAP = new Range(1, 64);

    /** The Range object for secondary bitmap */
    public final static Range OF_SECONDARY_BITMAP = new Range(65, 128);

    /** The Range object for tertiary bitmap */
    public final static Range OF_TERTIARY_BITMAP = new Range(129, 192);

    /**
     * Create a new Range object
     *
     * @param start The start of the range.
     * @param end The end of the range.
     * @return A new Range object.
     * @throws IllegalArgumentException if end value be less than the start value
     */
    public static Range of(int start, int end) {
        if (end < start)
            throw new IllegalArgumentException(String.format("The end value [%s] should not be less than the start value [%s]", end, start));

        return new Range(start, end);
    }

    /**
     * Return The start of the range object.
     *
     * @return The start of the range.
     */
    public int getStart() {
        return start;
    }

    /**
     * Return The end of the range object.
     *
     * @return The end of the range.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Convert Range object to String in log format.
     *
     * @return A string representation of the Range object in log format.
     */
    @Override
    public String toString() {
        return String.format("@Range[start: %s, end: %s]", start, end);
    }
}
