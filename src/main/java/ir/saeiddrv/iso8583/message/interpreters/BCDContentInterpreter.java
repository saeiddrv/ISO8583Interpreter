package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.ISOException;
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
    public byte[] transfer(String value, Charset charset) {
        return TypeUtils.textToByteArray(value);
    }

    @Override
    public String transfer(byte[] value, Charset charset) {
        return TypeUtils.byteArrayToText(value);
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) throws ISOException {

        int valueLength = value.length;
        boolean hasOddLength = valueLength % 2 != 0;

        if (hasOddLength) {
            if (!pad.hasPadding())
                throw new ISOException("FIELD[%d] length is odd and no pad have been set for it.", fieldNumber);
            value = pad.doPad(value, valueLength + 1);
        }

        byte[] pack = TypeUtils.byteArrayToBCD(value);
        return TypeUtils.encodeBytes(pack, charset);
    }
}
