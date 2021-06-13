package ir.saeiddrv.iso8583.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.TPDUHeaderInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Header Interpreter, TPDU")
public class TPDUHeaderInterpreterTests {

    private final Charset charset = StandardCharsets.ISO_8859_1;
    private final String expected = "6001210121";
    private final byte[] expectedBytes = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

    public TPDUHeaderInterpreter fromBytes() {
        byte protocolID = 0x60;                              // Decimal: 96
        byte[] sourceAddress = new byte[] {0x01, 0x21};      // Decimal: 289
        byte[] destinationAddress = new byte[] {0x01, 0x21}; // Decimal: 289
        return TPDUHeaderInterpreter.fromBytes(protocolID, sourceAddress, destinationAddress);
    }

    public TPDUHeaderInterpreter fromHexInteger() {
        int protocolID = 0x60;           // Decimal: 96
        int sourceAddress = 0x0121;      // Decimal: 289
        int destinationAddress = 0x0121; // Decimal: 289
        return TPDUHeaderInterpreter.fromInteger(protocolID, sourceAddress, destinationAddress);
    }

    public TPDUHeaderInterpreter fromInteger() {
        int protocolID = 96;           // hex: 0x60
        int sourceAddress = 289;       // hex: 0x0121
        int destinationAddress = 289;  // hex: 0x0121
        return TPDUHeaderInterpreter.fromInteger(protocolID, sourceAddress, destinationAddress);
    }

    public TPDUHeaderInterpreter fromHexString() {
        String protocolID = "60";           // hex: 0x60,   Decimal: 96
        String sourceAddress = "121";       // hex: 0x0121, Decimal: 289
        String destinationAddress = "121";  // hex: 0x0121, Decimal: 289
        return TPDUHeaderInterpreter.fromHexString(protocolID, sourceAddress, destinationAddress);
    }

    public TPDUHeaderInterpreter fromDecimal() {
        String protocolID = "60";           // hex: 0x60,   Decimal: 96
        String sourceAddress = "121";       // hex: 0x0121, Decimal: 289
        String destinationAddress = "121";  // hex: 0x0121, Decimal: 289
        return TPDUHeaderInterpreter.fromDecimal(protocolID, sourceAddress, destinationAddress);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromBytes")
    public void assertEqualCreateFromBytes() {
        byte[] actual = fromBytes().getValue();
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromInteger (hex numbers)")
    public void assertEqualCreateFromHexInt() {
        byte[] actual = fromHexInteger().getValue();
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromInteger")
    public void assertEqualCreateFromInt() {
        byte[] actual = fromInteger().getValue();
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromHexString")
    public void assertEqualCreateFromHexString() {
        byte[] actual = fromHexString().getValue();
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromDecimal (BCD)")
    public void assertEqualCreateFromDecimalString() {
        byte[] actual = fromDecimal().getValue();
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("CREATING (fromBytes) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromBytesAndValueToHexString() {
        String actual = fromBytes().getValueAsString();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromHexInteger) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromHexIntegerAndValueToHexString() {
        String actual = fromHexInteger().getValueAsString();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromInteger) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromIntegerAndValueToHexString() {
        String actual = fromInteger().getValueAsString();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromHexString) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromHexStringAndValueToHexString() {
        String actual = fromHexString().getValueAsString();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromDecimal) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromHDecimalAndValueToHexString() {
        String actual = fromDecimal().getValueAsString();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("PACKING: 6001210121")
    public void assertEqualPack() throws ISO8583Exception {
        byte[] actual = fromBytes().pack(charset);
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("UNPACKING: 6001210121")
    public void assertEqualUnPack() throws ISO8583Exception {
        // TPDU: 6001210121
        byte[] data = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        UnpackContentResult result = fromBytes().unpack(data, 0, charset);
        byte[] actual = result.getValue();
        assertArrayEquals(expectedBytes, actual);
    }

    @Test
    @DisplayName("UNPACKING: It should throw an exception when the message length is not long enough.")
    public void assertThrowsUnPack() {
        assertThrows(ISO8583Exception.class,
                () -> fromBytes().unpack(new byte[]{0x60, 0x01}, 0, charset));
    }

}
