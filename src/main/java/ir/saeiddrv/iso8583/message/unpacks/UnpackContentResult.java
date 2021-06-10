package ir.saeiddrv.iso8583.message.unpacks;

public class UnpackContentResult implements UnpackResult<byte[]> {

    private final byte[] value;
    private final int nextOffset;

    public UnpackContentResult(byte[] value, int nextOffset) {
        this.value = value;
        this.nextOffset = nextOffset;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public int getNextOffset() {
        return nextOffset;
    }
}
