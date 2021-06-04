package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import java.io.IOException;
import java.nio.charset.Charset;

public interface Field {

    public int getNumber();

    public boolean hasFormatter();

    public void setFormatter(FieldFormatter formatter);

    public String getFormatted();

    public void setDescription(String description);

    public String getValue();

    public String getDescription();

    public void clear();

    public byte[] pack(Charset charset) throws IOException, ISOMessageException;

}
