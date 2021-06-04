package ir.saeiddrv.iso8583.message.fields.formatters;

public interface FieldFormatter {

    public String getFormatted(int fieldNumber, String... value);

}
