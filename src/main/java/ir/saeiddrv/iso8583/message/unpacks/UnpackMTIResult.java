package ir.saeiddrv.iso8583.message.unpacks;

public class UnpackMTIResult implements UnpackResult<String> {

    private final String value;
    private final int nextOffset;

    public UnpackMTIResult(String value, int nextOffset) {
        this.value = value;
        this.nextOffset = nextOffset;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public int getNextOffset() {
        return nextOffset;
    }
}
