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

            String lengthHex = Integer.toHexString(messageBytesLength);
            byte[] lengthBytes = TypeUtils.hexStringToByteArray(lengthHex);
            int lengthBytesCount = lengthBytes.length;

            if (lengthBytesCount > count)
                throw new ISO8583Exception("The length count of the generated message " +
                        "(%s: %s bytes in HEX) is greater than the specified length (%s).",
                        messageBytesLength, lengthBytesCount, count);

            byte[] pad = (count > lengthBytesCount) ? new byte[count - lengthBytesCount] : new byte[0];

            byte[] pack = ByteBuffer.allocate(count).put(pad).put(lengthBytes).array();
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
        byte[] unpack = TypeUtils.encodeBytes(pack, charset);
        int length =  Integer.parseInt(TypeUtils.byteArrayToHexString(unpack), 16);
        return new UnpackLengthResult(length, endOffset);
    }
}
