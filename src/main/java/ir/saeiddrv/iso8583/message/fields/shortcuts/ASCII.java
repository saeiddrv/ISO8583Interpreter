package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.ASCIIContentInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.ASCIILengthInterpreter;
import java.nio.charset.Charset;

public class ASCII implements ShortcutField {

    private final LengthType lengthType;
    private final int maximumLength;
    private final ContentPad contentPad;
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    private ASCII(LengthType lengthType, int maximumLength, ContentPad contentPad) {
        this.lengthType = lengthType;
        this.maximumLength = maximumLength;
        this.contentPad = contentPad;
    }

    public static ASCII create(LengthType lengthType, int maximumLength) {
        return new ASCII(lengthType, maximumLength, ContentPad.NO_PADDING);
    }

    public static ASCII create(LengthType lengthType, int maximumLength, ContentPad contentPad) {
        return new ASCII(lengthType, maximumLength, contentPad);
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
                    new ASCIIContentInterpreter(),
                    contentPad);
        else
            field = SingleField.create(fieldNumber,
                    new ASCIILengthInterpreter(),
                    LengthValue.create(lengthType.getCount(), maximumLength),
                    new ASCIIContentInterpreter(),
                    contentPad);
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
