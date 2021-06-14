package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.BinaryBitmapInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.base.BitmapInterpreter;
import java.nio.charset.Charset;

public class BITMAP implements ShortcutField {

    private final BitmapType type;
    private final int length;
    private final Range range;
    private final BitmapInterpreter interpreter;
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    private BITMAP(BitmapType type, int length, Range range, BitmapInterpreter interpreter) {
        this.type = type;
        this.length = length;
        this.range = range;
        this.interpreter = interpreter;
    }

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
        Field field = BitmapField.create(fieldNumber, type, range, length, interpreter);
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
