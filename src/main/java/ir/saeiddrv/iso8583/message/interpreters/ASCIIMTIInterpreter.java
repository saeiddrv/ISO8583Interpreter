package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.base.MTIInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ASCIIMTIInterpreter implements MTIInterpreter {

    @Override
    public String getName() {
        return "ASCII MTI Interpreter";
    }

    @Override
    public byte[] pack(String mti, Charset charset) {
        // Packing data
        return TypeUtils.byteArrayToHexArray(TypeUtils.encodeBytes(mti, charset), charset);
    }

    @Override
    public UnpackMTIResult unpack(byte[] message,
                                  int offset,
                                  Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + 4;

        // Copying the data related to this unit
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);

        // Unpacking
        String unpack = TypeUtils.decodeBytes(pack, charset);

        // Creating result object
        return new UnpackMTIResult(unpack, endOffset);
    }

}
