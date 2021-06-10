package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import java.nio.charset.Charset;

public interface HeaderInterpreter {

    public String getName();

    public byte[] pack(Charset charset);

    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      Charset charset);
}
