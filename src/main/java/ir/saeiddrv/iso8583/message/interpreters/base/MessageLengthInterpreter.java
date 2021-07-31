package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import java.nio.charset.Charset;

public interface MessageLengthInterpreter {

    public String getName();

    public byte[] pack(int count,
                       int messageBytesLength,
                       Charset charset) throws ISO8583Exception;

    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int count,
                                     Charset charset) throws ISO8583Exception;

}
