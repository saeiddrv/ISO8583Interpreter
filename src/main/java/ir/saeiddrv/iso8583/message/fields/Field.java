package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import java.nio.charset.Charset;

public interface Field {

    public int getNumber();

    public boolean hasFormatter();

    public void setCharset(Charset charset);

    public void setDescription(String description);

    public byte[] getValue();

    public String getValueAsString();

    public void setValueFormatter(ValueFormatter formatter);

    public String getValueFormatted();

    public String getDescription();

    public void clear();

    public byte[] pack() throws ISO8583Exception;

    public int unpack(byte[] message, int offset) throws ISO8583Exception;
}
