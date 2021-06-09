package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.fields.Bitmap;
import ir.saeiddrv.iso8583.message.ISOException;
import java.nio.charset.Charset;

public interface BitmapInterpreter {

    public String getName();

    public byte[] pack(Bitmap bitmap, Charset charset) throws ISOException;

}
