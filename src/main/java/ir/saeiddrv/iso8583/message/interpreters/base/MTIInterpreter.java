package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.ISOMessageException;
import java.nio.charset.Charset;

public interface MTIInterpreter {

    public String getName();

    public byte[] pack(String mti, Charset charset) throws ISOMessageException;

}
