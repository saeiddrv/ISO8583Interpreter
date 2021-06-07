package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class Content {

    private final ContentInterpreter interpreter;
    private final ContentValue value;

    Content(ContentInterpreter interpreter, ContentPad contentPad, ContentType contentType) {
        this.interpreter = interpreter;
        this.value = new ContentValue(contentType, contentPad);
    }

    ContentInterpreter getInterpreter() {
        return interpreter;
    }

    public ContentValue getValue() {
        return value;
    }

    public ContentType getType() {
        return value.getType();
    }

    public boolean isRAW() {
        return getType() == ContentType.RAW;
    }

    public int getLength() {
        return value.getValue().length;
    }

    public void setValue(byte[] value) {
        this.value.setValue(value);
    }

    public void setValue(String value) {
        setValue(TypeUtils.stringToByteArray(value));
    }

    public String getValueAsString() {
        return TypeUtils.byteArrayToString(getValue().getValue());
    }

    public boolean hasPadding() {
        return value.hasPadding();
    }

    public void doPad(int maximumLength) {
        if (hasPadding())
            value.doPad(maximumLength);
    }

    public boolean hasInterpreter() {
        return interpreter != null;
    }

    byte[] pack(int filedNumber, LengthValue length, Charset charset) throws ISOMessageException {
        if (hasInterpreter())
            return interpreter.pack(filedNumber, length, value, charset);
        else
            return new byte[0];
    }

    @Override
    public String toString() {
        return String.format("@Content[value: %s, interpreter: %s]", value, interpreter.getName());
    }
}
