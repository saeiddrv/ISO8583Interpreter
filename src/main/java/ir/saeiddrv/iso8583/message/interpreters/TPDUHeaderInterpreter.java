package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.headers.HeaderContent;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.interpreters.base.HeaderInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.Arrays;

public class TPDUHeaderInterpreter implements HeaderInterpreter {

    @Override
    public String getName() {
        return "TPDU Header Interpreter";
    }

    @Override
    public byte[] pack(HeaderContent content, Charset charset) throws ISO8583Exception {
        byte[] value =  content.getValue();

        // Encoding data with charset
        return TypeUtils.encodeBytes(value, charset);
    }

    @Override
    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + 5;

        if (message.length < endOffset)
            throw new ISO8583Exception("UNPACKING ERROR, HEADER (%s): The received message length is less than the required amount. " +
                    "[messageLength: %s, startIndex: %s, endIndex: %s]", getName(), message.length, offset, endOffset);

        // Copying the data related to this unit and encode it with charset
        byte[] data = Arrays.copyOfRange(message, offset, endOffset);
        data = TypeUtils.encodeBytes(data, charset);

        // Creating result object
        return new UnpackContentResult(data, endOffset);
    }
}
