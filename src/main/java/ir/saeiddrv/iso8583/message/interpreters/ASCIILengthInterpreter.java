package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ASCIILengthInterpreter implements LengthInterpreter {

    @Override
    public String getName() {
        return "ASCII Length Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       int valueBytesLength,
                       Charset charset) {
        // Setting pad
        String lengthString = PadUtils.padLeft(String.valueOf(valueBytesLength), length.getCount(), '0');

        // Packing data
        byte[] data = TypeUtils.encodeBytes(lengthString, charset);
        return TypeUtils.byteArrayToHexArray(data, charset);
    }

    @Override
    public UnpackLengthResult unpack(byte[] message,
                                     int offset,
                                     int fieldNumber,
                                     LengthValue length,
                                     Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + length.getCount();

        if (message.length < endOffset)
            throw new ISO8583Exception("UNPACKING ERROR, Length (%s): The received message length is less than the required amount. " +
                    "[messageLength: %s, startIndex: %s, endIndex: %s]", getName(), message.length, offset, endOffset);

        // Copying the data related to this unit and encoding it with charset
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);

        // Unpacking from BCD coding and cast to Integer
        String unpack = TypeUtils.decodeBytes(pack, charset);
        int lengthNumber = Integer.parseInt(unpack);

        // Creating result object
        return new UnpackLengthResult(lengthNumber, endOffset);
    }

}
