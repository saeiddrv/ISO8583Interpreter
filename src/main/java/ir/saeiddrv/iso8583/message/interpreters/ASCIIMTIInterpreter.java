package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.interpreters.base.MTIInterpreter;
import java.nio.charset.Charset;

public class ASCIIMTIInterpreter implements MTIInterpreter {

    @Override
    public String getName() {
        return "ASCII MTI Interpreter";
    }

    @Override
    public byte[] pack(String mti, Charset charset) {
        return new byte[0];
    }

}
