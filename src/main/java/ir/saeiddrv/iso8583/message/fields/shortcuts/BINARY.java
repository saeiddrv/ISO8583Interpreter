package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.fields.ContentType;
import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.SingleField;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import ir.saeiddrv.iso8583.message.interpreters.BinaryContentInterpreter;

public class BINARY implements ShortcutField {

    private final int lengthOfBytes;
    private FieldFormatter formatter = null;
    private String description = "UNDEFINED";

    public BINARY(int lengthOfBytes) {
        this.lengthOfBytes = lengthOfBytes;
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
        Field field = SingleField.createFixed(fieldNumber,
                lengthOfBytes,
                new BinaryContentInterpreter(),
                ContentType.BINARY,
                ContentPad.NO_PADDING);
        field.setFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
