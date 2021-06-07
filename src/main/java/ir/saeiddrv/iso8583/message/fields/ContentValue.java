package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.PadDirection;
import java.util.Arrays;

public class ContentValue {

    private byte[] value = new byte[]{0};
    private final ContentType type;
    private final ContentPad pad;

    public ContentValue(ContentType type, ContentPad pad) {
        this.type = type;
        this.pad = pad;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public ContentType getType() {
        return type;
    }

    public ContentPad getPad() {
        return pad;
    }

    public boolean hasPadding() {
        return pad.getPadDirection() != PadDirection.NONE;
    }

    public void doPad(int maximumLength) {
        if (hasPadding())
            this.value = pad.doPad(value, maximumLength);
    }

    @Override
    public String toString() {
        return String.format("@ContentValue[value: %s, type: %s, pad: %s]", Arrays.toString(value), type, pad);
    }
}
