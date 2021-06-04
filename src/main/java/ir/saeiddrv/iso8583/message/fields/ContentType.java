package ir.saeiddrv.iso8583.message.fields;

public enum ContentType {
    RAW,
    BINARY,
    NUMBER,
    AMOUNT,
    CHARACTER,
    TRACK2,
    IRAN_SYSTEM;

    @Override
    public String toString() {
        return String.format("@ContentType[label: %s]", super.toString());
    }
}
