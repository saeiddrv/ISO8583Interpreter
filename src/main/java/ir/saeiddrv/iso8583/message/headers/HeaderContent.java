package ir.saeiddrv.iso8583.message.headers;

import java.nio.charset.Charset;

public interface HeaderContent {

    public void setCharset(Charset charset);

    public byte[] getValue();

    public String getValueAsString();
}
