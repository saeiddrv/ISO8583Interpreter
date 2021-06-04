package ir.saeiddrv.iso8583.message.fields;

public enum BitmapType {

    DATA,
    PRIMARY,
    SECONDARY,
    TERTIARY;

    @Override
    public String toString() {
        return String.format("@BitmapType[label: %s]", super.toString());
    }
}
