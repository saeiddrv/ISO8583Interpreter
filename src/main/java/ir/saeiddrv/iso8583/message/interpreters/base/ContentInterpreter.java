package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import java.nio.charset.Charset;

public interface ContentInterpreter {

    public String getName();

    public byte[] transfer(String value, Charset charset);

    public String transfer(byte[] value, Charset charset);

    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) throws ISOException;

}
