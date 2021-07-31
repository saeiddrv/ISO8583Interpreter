package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class COMBINE implements ShortcutField {

    private Charset charset = null;
    private LengthInterpreter lengthInterpreter = null;
    private LengthValue lengthValue = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";
    private final Map<Integer, Field> fields = new HashMap<>();

    private COMBINE() { }

    public static COMBINE create() {
        return new COMBINE();
    }

    public ShortcutField setLength(LengthType lengthType, int maximumLength, LengthInterpreter lengthInterpreter) {
        this.lengthValue = LengthValue.create(lengthType.getCount(), maximumLength);
        this.lengthInterpreter = lengthInterpreter;
        return this;
    }

    public ShortcutField addSubField(int fieldNumber, Field field) throws ISO8583Exception {
        if (!fields.containsKey(fieldNumber)) {
            field.setCharset(charset);
            fields.put(fieldNumber, field);
            return this;
        } else throw new ISO8583Exception("The SUBFIELD[%d] is already defined.", fieldNumber);
    }

    public ShortcutField addSubField(int fieldNumber, ShortcutField field) throws ISO8583Exception {
        if (!fields.containsKey(fieldNumber)) {
            field.setCharset(charset);
            fields.put(fieldNumber, field.toField(fieldNumber));
            return this;
        } else throw new ISO8583Exception("The SUBFIELD[%d] is already defined.", fieldNumber);
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
        if (lengthValue == null)
            field = CombineField.create(fieldNumber, fields.values().toArray(new Field[0]));
        else
            field = CombineField.create(fieldNumber,
                    lengthValue,
                    lengthInterpreter,
                    fields.values().toArray(new Field[0]));
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
