package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.SingleField;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import java.nio.charset.Charset;

public class RAW implements ShortcutField {

    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    private RAW() {}

    public static RAW create() {
        return new RAW();
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
        Field field = SingleField.createRaw(fieldNumber);
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
