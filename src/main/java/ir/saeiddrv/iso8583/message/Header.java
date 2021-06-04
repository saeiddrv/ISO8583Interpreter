package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.interpreters.base.HeaderInterpreter;
import java.nio.charset.Charset;

public class Header {

    private final HeaderInterpreter interpreter;

    Header(HeaderInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public HeaderInterpreter getInterpreter() {
        return interpreter;
    }

    public String getInterpreterName() {
        return interpreter.getName();
    }

    public byte[] pack(Charset charset) {
        return interpreter.pack(charset);
    }

    @Override
    public String toString() {
        return String.format("@Header[interpreter: %s]", getInterpreterName());
    }
}
