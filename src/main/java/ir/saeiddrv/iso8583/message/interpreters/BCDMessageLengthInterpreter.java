package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BCDMessageLengthInterpreter implements MessageLengthInterpreter {

    @Override
    public String getName() {
        return "BCD Message Length Interpreter";
    }

    @Override
    public byte[] pack(int count, int messageBytesLength, Charset charset) throws ISO8583Exception {
        if (count > 0) {
            if (count % 2 != 0)
                throw new ISO8583Exception("The message length count for BCD coding must be even.");

            String decimalLength = PadUtils.padLeft(String.valueOf(messageBytesLength), count * 2, '0');

            byte[] pack = TypeUtils.textToBCDBytes(decimalLength);
            return TypeUtils.encodeBytes(pack, charset);
        } else {
            return new byte[0]; // WITHOUT LENGTH
        }
    }

    @Override
    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int count,
                                     Charset charset) throws ISO8583Exception {
        int endOffset = offset + count;
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);
        pack = TypeUtils.encodeBytes(pack, charset);
        String unpack = TypeUtils.bcdBytesToText(pack);
        int length = Integer.parseInt(unpack);
        return new UnpackLengthResult(length, endOffset);
    }
}
