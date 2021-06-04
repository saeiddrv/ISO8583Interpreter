package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.interpreters.base.MTIInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;

public class BCDMTIInterpreter implements MTIInterpreter {

    @Override
    public String getName() {
        return "BCD MTI Interpreter";
    }

    @Override
    public byte[] pack(String mti, Charset charset) {
        return TypeUtils.decimalToBCD(mti);
    }
}
