package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import ir.saeiddrv.iso8583.message.interpreters.ASCIILengthInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.BCDContentInterpreter;

public class ASCIIBCD implements ShortcutField {

    private final LengthType lengthType;
    private final int maximumLength;
    private final ContentType contentType;
    private final ContentPad contentPad;
    private FieldFormatter formatter = null;
    private String description = "UNDEFINED";

    public static ASCIIBCD create(LengthType lengthType, int maximumLength, ContentType contentType, ContentPad contentPad) {
        return new ASCIIBCD(lengthType, maximumLength, contentType, contentPad);
    }

    private ASCIIBCD(LengthType lengthType, int maximumLength, ContentType contentType, ContentPad contentPad) {
        this.lengthType = lengthType;
        this.maximumLength = maximumLength;
        this.contentPad = contentPad;
        this.contentType = contentType;
    }

    @Override
    public ShortcutField setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ShortcutField setFormatter(FieldFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Field toField(int fieldNumber) {
        Field field = SingleField.create(fieldNumber,
                new ASCIILengthInterpreter(),
                LengthValue.create(lengthType.getCount(), maximumLength),
                new BCDContentInterpreter(),
                contentType,
                contentPad);
        field.setFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
