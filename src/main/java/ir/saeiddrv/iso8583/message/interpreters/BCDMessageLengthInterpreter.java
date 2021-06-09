package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class BCDMessageLengthInterpreter implements MessageLengthInterpreter {

    @Override
    public String getName() {
        return "BCD Message Length Interpreter";
    }

    @Override
    public byte[] pack(int count, int messageBytesLength, Charset charset) throws ISOException {
        if (count > 0) {
            if (count % 2 != 0)
                throw new ISOException("The message length count for BCD coding must be even.");

            String decimalLength = PadUtils.padLeft(String.valueOf(messageBytesLength), count * 2, '0');

            byte[] pack = TypeUtils.textToBCDBytes(decimalLength);
            return TypeUtils.encodeBytes(pack, charset);
        } else {
            return new byte[0]; // WITHOUT LENGTH
        }
    }
}
