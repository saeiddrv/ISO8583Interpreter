package ir.saeiddrv.iso8583.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.HexMessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Message Length Interpreter, Hex")
public class HexMessageLengthInterpreterTests {

    private final Charset charset = StandardCharsets.ISO_8859_1;
    private HexMessageLengthInterpreter interpreter;

    @BeforeEach
    public void setup() {
        interpreter = new HexMessageLengthInterpreter();
    }

    @Test
    @DisplayName("PACKING Message Length: HEX")
    public void assertEqualPack() throws ISO8583Exception {
        byte[] expected = new byte[]{0x00, 0x64};
        byte[] actual = interpreter.pack(2, 100, charset);
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("UNPACKING Message Length: HEX")
    public void assertEqualUnPack() throws ISO8583Exception {
        int expected = 100;
        UnpackLengthResult actual = interpreter.unpack(new byte[]{0x00, 0x64}, 0, 2, charset);
        assertEquals(expected, actual.getValue());
    }

}
