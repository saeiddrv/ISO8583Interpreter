package ir.saeiddrv.iso8583.message;

public enum PadDirection {
    NONE, LEFT, RIGHT;

    @Override
    public String toString() {
        return String.format("@PadDirection[label: %s]", super.toString());
    }
}
