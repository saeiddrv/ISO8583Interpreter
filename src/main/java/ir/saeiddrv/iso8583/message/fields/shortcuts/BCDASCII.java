package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import ir.saeiddrv.iso8583.message.interpreters.ASCIIContentInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.BCDLengthInterpreter;

public class BCDASCII implements ShortcutField {

    private final LengthType lengthType;
    private final int maximumLength;
    private final ContentType contentType;
    private final ContentPad contentPad;
    private FieldFormatter formatter = null;
    private String description = "UNDEFINED";

    public BCDASCII(LengthType lengthType, int maximumLength, ContentType contentType, ContentPad contentPad) {
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
                new BCDLengthInterpreter(),
                LengthValue.create(lengthType.getCount(), maximumLength),
                new ASCIIContentInterpreter(),
                contentType,
                contentPad);
        field.setFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
