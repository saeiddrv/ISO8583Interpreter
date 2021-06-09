package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISOException;
import java.nio.charset.Charset;

public interface MessageLengthInterpreter {

    public String getName();

    public byte[] pack(int count, int messageBytesLength, Charset charset) throws ISOException;

}
