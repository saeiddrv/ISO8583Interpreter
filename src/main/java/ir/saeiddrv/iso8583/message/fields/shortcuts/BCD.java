package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.BCDContentInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.BCDLengthInterpreter;

import java.nio.charset.Charset;

public class BCD implements ShortcutField {

    private final LengthType lengthType;
    private final int maximumLength;
    private final ContentPad contentPad;
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    public static BCD create(LengthType lengthType, int maximumLength) {
        return new BCD(lengthType, maximumLength, ContentPad.NO_PADDING);
    }

    public static BCD create(LengthType lengthType, int maximumLength, ContentPad contentPad) {
        return new BCD(lengthType, maximumLength, contentPad);
    }

    private BCD(LengthType lengthType, int maximumLength, ContentPad contentPad) {
        this.lengthType = lengthType;
        this.maximumLength = maximumLength;
        this.contentPad = contentPad;
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
        Field field;
        if (lengthType.isFixed())
            field = SingleField.createFixed(fieldNumber,
                    maximumLength,
                    new BCDContentInterpreter(),
                    contentPad);
        else
            field = SingleField.create(fieldNumber,
                    new BCDLengthInterpreter(),
                    LengthValue.create(lengthType.getCount(), maximumLength),
                    new BCDContentInterpreter(),
                    contentPad);
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
