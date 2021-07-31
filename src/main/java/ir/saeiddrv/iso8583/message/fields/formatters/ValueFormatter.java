package ir.saeiddrv.iso8583.message.fields.formatters;

public interface ValueFormatter {

    public String getFormatted(int fieldNumber, String value);

}
