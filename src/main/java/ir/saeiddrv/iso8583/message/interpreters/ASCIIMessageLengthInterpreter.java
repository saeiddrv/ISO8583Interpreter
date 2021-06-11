package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ASCIIMessageLengthInterpreter implements MessageLengthInterpreter {

    @Override
    public String getName() {
        return "ASCII Message Length Interpreter";
    }

    @Override
    public byte[] pack(int count, int messageBytesLength, Charset charset) {
        // Setting pad
        String length = PadUtils.padLeft(String.valueOf(messageBytesLength), count, '0');

        // Packing data
        byte[] data = TypeUtils.encodeBytes(length, charset);
        return TypeUtils.byteArrayToHexArray(data, charset);
    }

    @Override
    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int count,
                                     Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + count;

        // Copying the data related to this unit and encoding it with charset
        byte[] data = Arrays.copyOfRange(message, offset, endOffset);
        data = TypeUtils.encodeBytes(data, charset);

        // Length: byte[] -> Integer
        int length =  Integer.parseInt(TypeUtils.decodeBytes(data, charset));

        // Creating result object
        return new UnpackLengthResult(length, endOffset);
    }
}
