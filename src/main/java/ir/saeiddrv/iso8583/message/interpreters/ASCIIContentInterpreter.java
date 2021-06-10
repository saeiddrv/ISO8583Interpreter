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
        return TypeUtils.textToByteArray(value);
    }

    @Override
    public String transfer(byte[] value, Charset charset) {
        return TypeUtils.byteArrayToText(value);
    }

    @Override
    public byte[] pack(int fieldNumber,
                       LengthValue length,
                       byte[] value,
                       ContentPad pad,
                       Charset charset) {
        byte[] pack = TypeUtils.byteArrayToHexArray(value, charset);
        return TypeUtils.encodeBytes(pack, charset);
    }

    @Override
    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      int fieldNumber,
                                      int length,
                                      ContentPad pad,
                                      Charset charset) throws ISO8583Exception {
        int endOffset = offset + length;
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);
        pack = TypeUtils.encodeBytes(pack, charset);
        String value = new String(pack, charset);
        byte[] unpack = value.isEmpty() ? new byte[0] : TypeUtils.textToByteArray(value);
        return new UnpackContentResult(unpack, endOffset);
    }
}
