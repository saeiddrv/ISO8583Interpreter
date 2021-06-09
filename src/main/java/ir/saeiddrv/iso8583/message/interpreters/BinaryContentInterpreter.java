package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class BinaryContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "Binary Content Interpreter";
    }

    @Override
    public byte[] transfer(String value, Charset charset) {
        return TypeUtils.hexStringToByteArray(value);
    }

    @Override
    public String transfer(byte[] value, Charset charset) {
        return TypeUtils.byteArrayToHexString(value);
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) throws ISOException {

        int valueLength = value.length;
        int fixedLength = length.getMaximumValue();

        if (valueLength < fixedLength)
            value = pad.doPad(value, fixedLength);

        if (valueLength > fixedLength)
            throw new ISOException("FIELD[%d] length (%s) is larger than of defined length (%s).",
                    valueLength, fixedLength, fieldNumber);

        return TypeUtils.encodeBytes(value, charset);
    }
}
