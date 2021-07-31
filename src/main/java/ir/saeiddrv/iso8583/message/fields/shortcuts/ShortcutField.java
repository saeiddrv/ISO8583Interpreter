package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import java.nio.charset.Charset;

public interface ShortcutField {

    public ShortcutField setDescription(String description);

    public ShortcutField setValueFormatter(ValueFormatter formatter);

    public ShortcutField setCharset(Charset charset);

    public Field toField(int fieldNumber);

}
