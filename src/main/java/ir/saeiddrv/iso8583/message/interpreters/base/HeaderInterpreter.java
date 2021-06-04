package ir.saeiddrv.iso8583.message.interpreters.base;

import java.nio.charset.Charset;

public interface HeaderInterpreter {

    public String getName();

    public byte[] pack(Charset charset);
}
