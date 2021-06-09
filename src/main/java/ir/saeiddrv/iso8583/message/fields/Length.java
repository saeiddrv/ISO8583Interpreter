package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import java.nio.charset.Charset;

public class Length {

    private final LengthValue value;
    private final LengthInterpreter interpreter;

    Length(LengthValue lengthValue, LengthInterpreter interpreter) {
        this.value = lengthValue;
        this.interpreter = interpreter;
    }

    public boolean hasInterpreter() {
        return interpreter != null;
    }

    public LengthInterpreter getInterpreter() {
        return interpreter;
    }

    public boolean hasValue() {
        return value != null;
    }

    public LengthValue getValue() {
        return value;
    }

    public int getCount() {
        return value.getCount();
    }

    public int getMaximumValue() {
        return value.getMaximumValue();
    }

    public boolean isFixed() {
        return getCount() == 0;
    }

    public boolean isDefined() {
        return value.isDefined();
    }

    public byte[] pack(int fieldNumber, int valueBytesLength, Charset charset) throws ISOException {
        if (hasInterpreter())
            return interpreter.pack(fieldNumber, value, valueBytesLength, charset);
        else
            return new byte[0];
    }

    @Override
    public String toString() {
        return String.format("@Length[value: %s, interpreter: %s]",
                value, hasInterpreter() ? interpreter.getName() : "UNDEFINED");
    }
}
