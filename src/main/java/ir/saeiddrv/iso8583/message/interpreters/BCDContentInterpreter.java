package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class BCDContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "BCD Content Interpreter";
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
                       Charset charset) throws ISOMessageException {

        int valueLength = value.length;
        boolean hasOddLength = valueLength % 2 != 0;

        if (hasOddLength) {
            if (!pad.hasPadding())
                throw new ISOMessageException("FIELD[%d] length is odd and no pad have been set for it.", fieldNumber);
            value = pad.doPad(value, valueLength + 1);
        }

        return TypeUtils.byteArrayToBCD(value);
    }
}
