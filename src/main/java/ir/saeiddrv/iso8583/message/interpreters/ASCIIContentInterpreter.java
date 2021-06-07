package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.ContentValue;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class ASCIIContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "ASCII Content Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber, LengthValue length, ContentValue content, Charset charset) {
        return TypeUtils.byteArrayToHexArray(content.getValue(), charset);
    }
}
