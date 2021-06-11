package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BinaryContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "Binary Content Interpreter";
    }

    @Override
    public byte[] transfer(String value, Charset charset) {
        byte[] bytes = TypeUtils.hexStringToByteArray(value);
        return TypeUtils.encodeBytes(bytes, charset);
    }

    @Override
    public String transfer(byte[] value, Charset charset) {
        value = TypeUtils.encodeBytes(value, charset);
        return TypeUtils.byteArrayToHexString(value);
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) throws ISO8583Exception {

        // Finding data length
        int valueLength = value.length;

        // Checking length of the data
        int fixedLength = length.getMaximumValue();
        if (valueLength > fixedLength)
            throw new ISO8583Exception("FIELD[%d] length (%s) is larger than of defined length (%s).",
                    valueLength, fixedLength, fieldNumber);

        // Setting pad (if necessary)
        if (valueLength < fixedLength) value = pad.doPad(value, fixedLength);

        // Encoding data with charset
        return TypeUtils.encodeBytes(value, charset);
    }

    @Override
    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      int fieldNumber,
                                      int length,
                                      ContentPad pad,
                                      Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + length;

        // Copying the data related to this unit and encoding it with charset
        byte[] data = Arrays.copyOfRange(message, offset, endOffset);
        data = TypeUtils.encodeBytes(data, charset);

        // Creating result object
        return new UnpackContentResult(data, endOffset);
    }
}
