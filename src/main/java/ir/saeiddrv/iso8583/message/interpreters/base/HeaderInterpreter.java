package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.headers.HeaderContent;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import java.nio.charset.Charset;

public interface HeaderInterpreter {

    public String getName();

    public byte[] pack(HeaderContent content, Charset charset) throws ISO8583Exception;

    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      Charset charset) throws ISO8583Exception;
}
