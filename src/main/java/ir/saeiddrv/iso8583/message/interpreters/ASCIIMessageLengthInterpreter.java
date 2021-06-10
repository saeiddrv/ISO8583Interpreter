package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;

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

    @Override
    public UnpackLengthResult unpack(byte[] message, int offset, int count, Charset charset) throws ISO8583Exception {
        return null;
    }
}
