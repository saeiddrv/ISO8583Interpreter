package ir.saeiddrv.iso8583.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.BCDMTIInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MTI Interpreter, BCD")
public class BCDMTIInterpreterTests {

    private final Charset charset = StandardCharsets.ISO_8859_1;
    private BCDMTIInterpreter interpreter;

    @BeforeEach
    public void setup() {
        interpreter = new BCDMTIInterpreter();
    }

    @Test
    @DisplayName("PACKING MTI: 0200")
    public void assertEqualPack() throws ISO8583Exception {
        byte[] expected = new byte[]{0x02, 0x00};
        byte[] actual = interpreter.pack("0200", charset);
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("UNPACKING MTI: 0200")
    public void assertEqualUnPack() throws ISO8583Exception {
        String expected = "0200";
        UnpackMTIResult actual = interpreter.unpack(new byte[]{0x02, 0x00}, 0, charset);
        assertEquals(expected, actual.getValue());
    }

    @Test
    @DisplayName("UNPACKING MTI: It should throw an exception when the message length is not long enough.")
    public void assertThrowsUnPack() {
        assertThrows(ISO8583Exception.class,
                () -> interpreter.unpack(new byte[]{0x02, 0x00}, 1, charset));
    }

}
