package ir.saeiddrv.iso8583.message.unpacks;

public class UnpackLengthResult implements UnpackResult<Integer> {

    private final int value;
    private final int nextOffset;

    public UnpackLengthResult(int value, int nextOffset) {
        this.value = value;
        this.nextOffset = nextOffset;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public int getNextOffset() {
        return nextOffset;
    }
}
