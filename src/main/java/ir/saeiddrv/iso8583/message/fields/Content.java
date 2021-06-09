package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Content {

    private final ContentInterpreter interpreter;
    private final ContentPad pad;
    private byte[] value = new byte[]{0};

    Content(ContentInterpreter interpreter, ContentPad contentPad) {
        this.interpreter = interpreter;
        this.pad = contentPad;
    }

    public byte[] getValue() {
        return value;
    }

    public String getValueAsString(Charset charset) {
        if (hasInterpreter()) return interpreter.transfer(value, charset);
        return TypeUtils.byteArrayToText(getValue());
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setValue(String value, Charset charset) {
        if (hasInterpreter()) setValue(interpreter.transfer(value, charset));
        else setValue(TypeUtils.textToByteArray(value));
    }

    public void doPad(int maximumLength) {
        value = pad.doPad(value, maximumLength);
    }

    public boolean hasInterpreter() {
        return interpreter != null;
    }

    public byte[] pack(int filedNumber, LengthValue length, Charset charset) throws ISOException {
        if (hasInterpreter())
            return interpreter.pack(filedNumber, length, value, pad, charset);
        else
            return value;
    }

    @Override
    public String toString() {
        return String.format("@Content[value: %s, pad: %s, interpreter: %s]",
                Arrays.toString(value), pad, interpreter.getName());
    }
}
