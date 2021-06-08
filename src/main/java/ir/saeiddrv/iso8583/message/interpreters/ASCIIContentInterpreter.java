package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.ContentPad;
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
    public byte[] transfer(String value) {
        return TypeUtils.stringToByteArray(value);
    }

    @Override
    public String transfer(byte[] value) {
        return TypeUtils.byteArrayToString(value);
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) {
        return TypeUtils.byteArrayToHexArray(value, charset);
    }
}
