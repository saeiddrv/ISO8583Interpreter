package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import java.nio.charset.Charset;

public interface MTIInterpreter {

    public String getName();

    public byte[] pack(String mti, Charset charset) throws ISO8583Exception;

    public UnpackMTIResult unpack(byte[] message,
                                  int offset,
                                  Charset charset) throws ISO8583Exception;

}
