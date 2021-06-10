package ir.saeiddrv.iso8583.message.interpreters.base;

import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.unpacks.UnpackBitmapResult;
import ir.saeiddrv.iso8583.message.fields.Bitmap;
import ir.saeiddrv.iso8583.message.ISO8583Exception;
import java.nio.charset.Charset;

public interface BitmapInterpreter {

    public String getName();

    public byte[] pack(Bitmap bitmap,
                       Charset charset) throws ISO8583Exception;

    public UnpackBitmapResult unpack(byte[] message,
                                     int offset,
                                     int length,
                                     Range range,
                                     Charset charset) throws ISO8583Exception;

}
