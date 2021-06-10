package ir.saeiddrv.iso8583.message.unpacks;

public class UnpackBitmapResult implements UnpackResult<int[]> {

    private final int[] value;
    private final int nextOffset;

    public UnpackBitmapResult(int[] value, int nextOffset) {
        this.value = value;
        this.nextOffset = nextOffset;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    public int getNextOffset() {
        return nextOffset;
    }
}
