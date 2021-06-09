package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import java.nio.charset.Charset;

public interface LengthInterpreter {

    public String getName();

    public byte[] pack(int fieldNumber, LengthValue length, int valueBytesLength, Charset charset) throws ISOException;

}
