package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import java.nio.charset.Charset;

public class ASCIIMessageLengthInterpreter implements MessageLengthInterpreter {

    @Override
    public String getName() {
        return "ASCII Message Length Interpreter";
    }

    @Override
    public byte[] pack(int count, int messageBytesLength, Charset charset) {
        return new byte[0];
    }
}
