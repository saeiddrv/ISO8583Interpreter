package ir.saeiddrv.iso8583.message.fields;

public class LengthValue {

    public static LengthValue UNDEFINED = new LengthValue(-1, -1);

    private final int count;
    private final int maximumValue;

    public static LengthValue create(int count, int maximumValue) {
        if (count < 0)
            throw new IllegalArgumentException("Invalid value " +
                    "(LengthValue: 'count' must be a positive number): " + count);
        if (maximumValue < 0)
            throw new IllegalArgumentException("Invalid value " +
                    "(LengthValue: 'maximumValue' must be a positive number): " + maximumValue);
        return new LengthValue(count, maximumValue);
    }

    private LengthValue(int count, int maximumValue) {
        this.count = count;
        this.maximumValue = maximumValue;
    }

    public int getCount() {
        return count;
    }

    public int getMaximumValue() {
        return maximumValue;
    }

    public boolean isDefined() {
        return count < 0;
    }

    public String getCountLiteral() {
        int count = getCount();
        if (count == 0)
            return "FIXED";
        else if (count > 0)
            return String.format("%sVAR", new String(new char[count]).replace("\0", "L"));
        else
            return "UNDEFINED";
    }

    @Override
    public String toString() {
        if (isDefined())
            return "@LengthValue[count: UNDEFINED, maximumValue: UNDEFINED]";
        else
            return String.format("@LengthValue[count: %s (%s), maximumValue: %s]", count, getCountLiteral(), maximumValue);
    }
}
