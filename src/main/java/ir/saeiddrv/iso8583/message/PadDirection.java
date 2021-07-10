package ir.saeiddrv.iso8583.message;

/**
 * Padding directions that can be used.
 *
 * @author Saeid Darvish
 */
public enum PadDirection {

    /** Without padding */
    NONE,

    /** Left padding */
    LEFT,

    /** Right padding */
    RIGHT;

    /**
     * Convert Padding direction to String in log format.
     *
     * @return A string representation of the Padding direction in log format.
     */
    @Override
    public String toString() {
        return String.format("@PadDirection[label: %s]", super.toString());
    }
}
