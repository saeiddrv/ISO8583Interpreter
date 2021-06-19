package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.PadDirection;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BCDContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "BCD Content Interpreter";
    }

    @Override
    public byte[] transfer(String value, Charset charset) {
        return TypeUtils.encodeBytes(value, charset);
    }

    @Override
    public String transfer(byte[] value, Charset charset) {
        return TypeUtils.decodeBytes(value, charset);
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) throws ISO8583Exception {

        int valueLength = value.length;

        // Setting pad (if necessary)
        if (valueLength % 2 != 0) {
            if (!pad.hasPadding())
                throw new ISO8583Exception("FIELD[%d] length is odd and no pad have been set for it.", fieldNumber);
            value = pad.doPad(value, valueLength + 1);
        }

        // Packing data by BCD coding
        byte[] pack = TypeUtils.byteArrayToBCD(value);

        // Encoding data with charset
        return TypeUtils.encodeBytes(pack, charset);
    }

    @Override
    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      int fieldNumber,
                                      int length,
                                      ContentPad pad,
                                      Charset charset) throws ISO8583Exception {

        // Finding the latest data position
        int endOffset = offset + TypeUtils.findPreferredLengthInBCD(length);

        if (message.length < endOffset)
            throw new ISO8583Exception("UNPACKING ERROR, Content (%s): The received message length is less than the required amount. " +
                    "[messageLength: %s, startIndex: %s, endIndex: %s]", getName(), message.length, offset, endOffset);

        // Copying the data related to this unit
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);

        // Unpacking from BCD coding
        String unpackText = TypeUtils.bcdBytesToText(pack);
        byte[] unpack = TypeUtils.encodeBytes(unpackText, charset);

        // Undoing pad (if necessary)
        if ((length % 2) != 0)
            if (pad.hasPadding())
                if (pad.getPadDirection() == PadDirection.LEFT)
                    unpack = Arrays.copyOfRange(unpack, 1, unpack.length);
                else if (pad.getPadDirection() == PadDirection.RIGHT)
                    unpack = Arrays.copyOfRange(unpack, 0, unpack.length - 1);

        // Creating result object
        return new UnpackContentResult(unpack, endOffset);
    }
}
