package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BCDLengthInterpreter implements LengthInterpreter {

    @Override
    public String getName() {
        return "BCD Length Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber, LengthValue length, int valueBytesLength, Charset charset) throws ISO8583Exception {
        if (valueBytesLength > length.getMaximumValue())
            throw new ISO8583Exception("Length of FIELD[%d] value is larger than allowed maximum size (%d).",
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

    @Override
    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int fieldNumber,
                                     LengthValue length,
                                     Charset charset) throws ISO8583Exception {
        int endOffset = offset + TypeUtils.findPreferredLengthInBCD(length.getCount());
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);
        pack = TypeUtils.encodeBytes(pack, charset);
        String unpack = TypeUtils.bcdBytesToText(pack);
        int lengthNumber = Integer.parseInt(unpack);
        return new UnpackLengthResult(lengthNumber, endOffset);
    }

}
