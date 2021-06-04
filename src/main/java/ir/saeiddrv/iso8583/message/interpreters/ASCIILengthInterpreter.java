package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import java.nio.charset.Charset;

public class ASCIILengthInterpreter implements LengthInterpreter {

    @Override
    public String getName() {
        return "ASCII Length Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber, LengthValue length, int valueBytesLength, Charset charset) {
        return new byte[0];
    }

}
