package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import ir.saeiddrv.iso8583.message.interpreters.BinaryBitmapInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.base.BitmapInterpreter;

public class BITMAP implements ShortcutField {

    private final BitmapType type;
    private final int length;
    private final Range range;
    private final BitmapInterpreter interpreter;
    private FieldFormatter formatter = null;
    private String description = "UNDEFINED";

    public static BITMAP create(BitmapType type, int length, Range range) {
        return new BITMAP(type, length, range, new BinaryBitmapInterpreter());
    }

    static BITMAP create(BitmapType type, int length, Range range, BitmapInterpreter interpreter) {
        return new BITMAP(type, length, range, interpreter);
    }

    @Override
    public BITMAP setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ShortcutField setFormatter(FieldFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    private BITMAP(BitmapType type, int length, Range range, BitmapInterpreter interpreter) {
        this.type = type;
        this.length = length;
        this.range = range;
        this.interpreter = interpreter;
    }

    @Override
    public Field toField(int fieldNumber) {
        Field field = BitmapField.create(fieldNumber, type, range, length, interpreter);
        field.setFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
