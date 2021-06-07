package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.SingleField;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;

public class RAW implements ShortcutField {

    private FieldFormatter formatter = null;
    private String description = "UNDEFINED";

    public static RAW create() {
        return new RAW();
    }

    private RAW() {}

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
        Field field = SingleField.createRaw(fieldNumber);
        field.setFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
