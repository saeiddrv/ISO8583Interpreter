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
            // Checking count of the length
            if (count % 2 != 0)
                throw new ISO8583Exception("The message length count for BCD coding must be even.");

            // Setting pad
            String decimalLength = PadUtils.padLeft(String.valueOf(messageBytesLength), count * 2, '0');

            // Encoding data with charset
            byte[] data = TypeUtils.encodeBytes(decimalLength, charset);

            // Packing data by BCD coding
            return TypeUtils.byteArrayToBCD(data);
        } else {
            return new byte[0]; // WITHOUT LENGTH
        }
    }

    @Override
    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int count,
                                     Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + count;

        if (message.length < endOffset)
            throw new ISO8583Exception("UNPACKING ERROR, MessageLength (%s): The received message length is less than the required amount. " +
                    "[messageLength: %s, startIndex: %s, endIndex: %s]", getName(), message.length, offset, endOffset);

        // Copying the data related to this unit and encoding it with charset
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);
        pack = TypeUtils.encodeBytes(pack, charset);

        // Unpacking from BCD coding and cast to Integer
        String unpack = TypeUtils.bcdBytesToText(pack);
        int length = Integer.parseInt(unpack);

        // Creating result object
        return new UnpackLengthResult(length, endOffset);
    }
}
