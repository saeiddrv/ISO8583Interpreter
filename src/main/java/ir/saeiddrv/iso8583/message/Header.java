package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.headers.HeaderContent;
import ir.saeiddrv.iso8583.message.interpreters.base.HeaderInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import java.nio.charset.Charset;

public class Header {

    private final HeaderContent content;
    private final HeaderInterpreter interpreter;

    Header(HeaderContent content, HeaderInterpreter interpreter) {
        this.content = content;
        this.interpreter = interpreter;
    }

    public HeaderContent getContent() {
        return content;
    }

    public byte[] getValue() {
        return content.getValue();
    }

    public String getValueAsString() {
        return content.getValueAsString();
    }

    public byte[] pack(Charset charset) throws ISO8583Exception {
        return interpreter.pack(content, charset);
    }

    public UnpackContentResult unpack(byte[] message, int offset, Charset charset) throws ISO8583Exception {
        return interpreter.unpack(message, offset, charset);
    }

    @Override
    public String toString() {
        return String.format("@Header[content: %s, interpreter: %s]", content, interpreter.getName());
    }
}
