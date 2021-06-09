package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class HexMessageLengthInterpreter implements MessageLengthInterpreter {

    @Override
    public String getName() {
        return "HEX Message Length Interpreter";
    }

    @Override
    public byte[] pack(int count, int messageBytesLength, Charset charset) throws ISOException {
        if (count > 0) {

            String lengthHex = Integer.toHexString(messageBytesLength);
            byte[] lengthBytes = TypeUtils.hexStringToByteArray(lengthHex);
            int lengthBytesCount = lengthBytes.length;

            if (lengthBytesCount > count)
                throw new ISOException("The length count of the generated message " +
                        "(%s: %s bytes in HEX) is greater than the specified length (%s).",
                        messageBytesLength, lengthBytesCount, count);

            byte[] pad = (count > lengthBytesCount) ? new byte[count - lengthBytesCount] : new byte[0];

            return ByteBuffer.allocate(count).put(pad).put(lengthBytes).array();
        } else {
            return new byte[0]; // WITHOUT LENGTH
        }
    }
}
