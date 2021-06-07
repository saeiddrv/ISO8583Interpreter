package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.fields.ContentValue;
import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.fields.LengthValue;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class BCDContentInterpreter implements ContentInterpreter {

    @Override
    public String getName() {
        return "BCD Content Interpreter";
    }

    @Override
    public byte[] pack(int fieldNumber, LengthValue length, ContentValue content, Charset charset) throws ISOMessageException {
        int contentLength = content.getValue().length;
        boolean hasOddLength = contentLength % 2 != 0;

        if (hasOddLength) {
            if (!content.hasPadding())
                throw new ISOMessageException("FIELD[%d] length is odd and no pad have been set for it.", fieldNumber);
            content.doPad(contentLength + 1);
        }

        return TypeUtils.byteArrayToBCD(content.getValue());
    }
}
