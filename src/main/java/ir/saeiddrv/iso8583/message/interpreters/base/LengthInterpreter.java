package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import java.nio.charset.Charset;

public interface LengthInterpreter {

    public String getName();

    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       int valueBytesLength,
                       Charset charset) throws ISO8583Exception;

    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int fieldNumber,
                                     LengthValue length,
                                     Charset charset) throws ISO8583Exception;

}
