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
    public byte[] pack(String mti, Charset charset) {
        byte[] pack = TypeUtils.textToBCDBytes(mti);
        return TypeUtils.encodeBytes(pack, charset);
    }

    @Override
    public UnpackMTIResult unpack(byte[] message,
                                  int offset,
                                  Charset charset) throws ISO8583Exception {
        int endOffset = offset + 2;
        byte[] pack = Arrays.copyOfRange(message, offset, offset + 2);
        pack = TypeUtils.encodeBytes(pack, charset);
        String unpack =  TypeUtils.bcdBytesToText(pack);
        return new UnpackMTIResult(unpack, endOffset);
    }
}
