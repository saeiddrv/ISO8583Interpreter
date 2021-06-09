package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class BCDLengthInterpreter implements LengthInterpreter {

    @Override
    public String getName() {
        return "BCD Length Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber, LengthValue length, int valueBytesLength, Charset charset) throws ISOException {
        if (valueBytesLength > length.getMaximumValue())
            throw new ISOException("Length of FIELD[%d] value is larger than allowed maximum size (%d).",
                    fieldNumber, length.getMaximumValue());

        int lengthCount = length.getCount();
        if (lengthCount > 0) {
            int bcdLength = TypeUtils.findPreferredLengthInBCD(lengthCount);
            String decimalLength = PadUtils.padLeft(String.valueOf(valueBytesLength), bcdLength * 2, '0');

            byte[] pack = TypeUtils.textToBCDBytes(decimalLength);
            return TypeUtils.encodeBytes(pack, charset);
        } else {
            return new byte[0]; // FIXED LENGTH
        }
    }

}
