package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.BinaryContentInterpreter;
import java.nio.charset.Charset;

public class BINARY implements ShortcutField {

    private final int lengthOfBytes;
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    private BINARY(int lengthOfBytes) {
        this.lengthOfBytes = lengthOfBytes;
    }

    public static BINARY create(int lengthOfBytes) {
        return new BINARY(lengthOfBytes);
    }

    @Override
    public ShortcutField setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ShortcutField setValueFormatter(ValueFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public ShortcutField setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public Field toField(int fieldNumber) {
        Field field = SingleField.createFixed(fieldNumber,
                lengthOfBytes,
                new BinaryContentInterpreter(),
                ContentPad.NO_PADDING);
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
