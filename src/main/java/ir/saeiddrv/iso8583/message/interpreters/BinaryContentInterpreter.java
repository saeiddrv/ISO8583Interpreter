package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.ContentValue;
import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import java.nio.charset.Charset;

public class BinaryContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "Binary Content Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber, LengthValue length, ContentValue content, Charset charset) throws ISOMessageException {
        return content.getValue();
    }
}
