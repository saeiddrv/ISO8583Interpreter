package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.ASCIIContentInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.BCDLengthInterpreter;

import java.nio.charset.Charset;

public class BCDASCII implements ShortcutField {

    private final LengthType lengthType;
    private final int maximumLength;
    private final ContentPad contentPad;
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    public static BCDASCII create(LengthType lengthType, int maximumLength) {
        return new BCDASCII(lengthType, maximumLength, ContentPad.NO_PADDING);
    }

    public static BCDASCII create(LengthType lengthType, int maximumLength, ContentPad contentPad) {
        return new BCDASCII(lengthType, maximumLength, contentPad);
    }

    private BCDASCII(LengthType lengthType, int maximumLength, ContentPad contentPad) {
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
        Field field = SingleField.create(fieldNumber,
                new BCDLengthInterpreter(),
                LengthValue.create(lengthType.getCount(), maximumLength),
                new ASCIIContentInterpreter(),
                contentPad);
        field.setCharset(charset);
        field.setValueFormatter(formatter);
        field.setDescription(description);
        return field;
    }
}
