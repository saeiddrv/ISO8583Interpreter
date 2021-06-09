package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.Bitmap;
import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.interpreters.base.BitmapInterpreter;
import java.nio.charset.Charset;

public class BinaryBitmapInterpreter implements BitmapInterpreter {

    @Override
    public String getName() {
        return "Binary Bitmap Interpreter";
    }

    @Override
    public byte[] pack(Bitmap bitmap, Charset charset) throws ISOException {
        return bitmap.getValue();
    }
}
