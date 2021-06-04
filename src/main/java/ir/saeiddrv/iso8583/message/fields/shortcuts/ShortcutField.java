package ir.saeiddrv.iso8583.message.fields.shortcuts;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;

public interface ShortcutField {

    public ShortcutField setDescription(String description);

    public ShortcutField setFormatter(FieldFormatter formatter);

    public Field toField(int fieldNumber);

}
