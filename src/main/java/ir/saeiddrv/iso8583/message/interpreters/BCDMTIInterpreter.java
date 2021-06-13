package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import ir.saeiddrv.iso8583.message.interpreters.base.MTIInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BCDMTIInterpreter implements MTIInterpreter {

    @Override
    public String getName() {
        return "BCD MTI Interpreter";
    }

    @Override
    public byte[] pack(String mti, Charset charset) throws ISO8583Exception {
        // Encoding data with charset
        byte[] data =  TypeUtils.encodeBytes(mti, charset);

        // Packing data by BCD coding
        return TypeUtils.byteArrayToBCD(data);
    }

    @Override
    public UnpackMTIResult unpack(byte[] message,
                                  int offset,
                                  Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + 2;

        if (message.length < endOffset)
            throw new ISO8583Exception("UNPACKING ERROR, MTI: The received message length is less than the required amount. " +
                    "[messageLength: %s]: [startIndex: %s, endIndex: %s]", message.length, offset, endOffset);

        // Copying the data related to this unit and encoding it with charset
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);
        pack = TypeUtils.encodeBytes(pack, charset);

        // Unpacking from BCD coding
        String unpack = TypeUtils.bcdBytesToText(pack);

        // Creating result object
        return new UnpackMTIResult(unpack, endOffset);
    }
}
