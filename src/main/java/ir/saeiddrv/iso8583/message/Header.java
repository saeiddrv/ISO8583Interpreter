package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.interpreters.base.HeaderInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import java.nio.charset.Charset;

public class Header {

    private final HeaderInterpreter interpreter;

    Header(HeaderInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public byte[] getValue() {
        return interpreter.getValue();
    }

    public String getValueAsString() {
        return interpreter.getValueAsString();
    }

    public byte[] pack(Charset charset) throws ISO8583Exception {
        return interpreter.pack(charset);
    }

    public UnpackContentResult unpack(byte[] message, int offset, Charset charset) throws ISO8583Exception {
        return interpreter.unpack(message, offset, charset);
    }

    @Override
    public String toString() {
        return String.format("@Header[value: %s, interpreter: %s]", getValueAsString(), interpreter.getName());
    }
}
