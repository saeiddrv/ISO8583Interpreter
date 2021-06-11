package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ASCIIContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "ASCII Content Interpreter";
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
                       Charset charset) {
        // Packing data
        byte[] pack = TypeUtils.byteArrayToHexArray(value, charset);

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
        int endOffset = offset + length;

        // Copying the data related to this unit
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);

        // Unpacking data
        byte[] unpack = TypeUtils.encodeBytes(pack, charset);

        // Creating result object
        return new UnpackContentResult(unpack, endOffset);
    }
}
