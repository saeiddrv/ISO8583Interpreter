package ir.saeiddrv.iso8583.message.fields;

public enum LengthType {

    FIXED(0),
    L(1),
    LL(2),
    LLL(3),
    LLLL(4),
    LLLLL(5),
    LLLLLL(6);

    private final int count;
    LengthType(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public boolean isFixed() {
        return count == 0;
    }

    public String getLiteral() {
        return super.toString();
    }

    @Override
    public String toString() {
        return String.format("@LengthType[count: %s, label: %s]", count, getLiteral());
    }
}
