package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class HexMessageLengthInterpreter implements MessageLengthInterpreter {

    @Override
    public String getName() {
        return "HEX Message Length Interpreter";
    }

    @Override
    public byte[] pack(int count, int messageBytesLength, Charset charset) throws ISO8583Exception {
        if (count > 0) {
            // Length: Integer -> HEX -> byte[]
            byte[] data = TypeUtils.hexStringToByteArray(Integer.toHexString(messageBytesLength));

            // Checking count of the length bytes
            int lengthCount = data.length;
            if (lengthCount > count)
                throw new ISO8583Exception("The length count of the generated message " +
                        "(%s: %s bytes in HEX) is greater than the specified length (%s).",
                        messageBytesLength, lengthCount, count);

            // Setting left-pad by zero (if necessary)
            byte[] padData = (count > lengthCount) ? new byte[count - lengthCount] : new byte[0];
            data = ByteBuffer.allocate(count).put(padData).put(data).array();

            // Encoding data with charset
            return TypeUtils.encodeBytes(data, charset);
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
            throw new ISO8583Exception("UNPACKING ERROR, MessageLength(HEX): The received message length is less than the required amount. " +
                    "[messageLength: %s]: [startIndex: %s, endIndex: %s]", message.length, offset, endOffset);

        // Copying the data related to this unit and encoding it with charset
        byte[] data = Arrays.copyOfRange(message, offset, endOffset);
        data = TypeUtils.encodeBytes(data, charset);

        // Length: byte[] -> HEX -> Integer
        int length =  Integer.parseInt(TypeUtils.byteArrayToHexString(data), 16);

        // Creating result object
        return new UnpackLengthResult(length, endOffset);
    }
}
