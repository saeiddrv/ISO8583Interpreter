package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class Content {

    private final ContentInterpreter interpreter;
    private final ContentPad pad;
    private byte[] value = new byte[]{0};

    Content(ContentInterpreter interpreter, ContentPad contentPad) {
        this.interpreter = interpreter;
        this.pad = contentPad;
    }

    byte[] pack(int filedNumber, LengthValue length, Charset charset) throws ISOMessageException {
        if (hasInterpreter())
            return interpreter.pack(filedNumber, length, value, pad, charset);
        else
            return new byte[0];
    }

    public byte[] getValue() {
        return value;
    }

    public String getValueAsString() {
        if (hasInterpreter()) return interpreter.transfer(getValue());
        return TypeUtils.byteArrayToString(getValue());
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setValue(String value) {
        if (hasInterpreter()) setValue(interpreter.transfer(value));
        else setValue(TypeUtils.stringToByteArray(value));
    }

    public void doPad(int maximumLength) {
        value = pad.doPad(value, maximumLength);
    }

    public boolean hasInterpreter() {
        return interpreter != null;
    }

    @Override
    public String toString() {
        return String.format("@Content[value: %s, pad: %s, interpreter: %s]",
                getValueAsString(), pad, interpreter.getName());
    }
}
