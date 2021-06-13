package ir.saeiddrv.iso8583.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.TPDUHeaderInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Header Interpreter, TPDU")
public class TPDUHeaderInterpreterTests {

    private final Charset charset = StandardCharsets.ISO_8859_1;

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
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        byte[] actual = fromBytes().getValue();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromInteger (hex numbers)")
    public void assertEqualCreateFromHexInt() {
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        byte[] actual = fromHexInteger().getValue();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromInteger")
    public void assertEqualCreateFromInt() {
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        byte[] actual = fromInteger().getValue();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromHexString")
    public void assertEqualCreateFromHexString() {
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        byte[] actual = fromHexString().getValue();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING OBJECT: fromDecimal (BCD)")
    public void assertEqualCreateFromDecimalString() {
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        byte[] actual = fromDecimal().getValue();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromBytes) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromBytesAndValueToHexString() {
        String expected = "6001210121";
        String actual = TypeUtils.byteArrayToHexString(fromBytes().getValue());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromHexInteger) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromHexIntegerAndValueToHexString() {
        String expected = "6001210121";
        String actual = TypeUtils.byteArrayToHexString(fromHexInteger().getValue());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromInteger) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromIntegerAndValueToHexString() {
        String expected = "6001210121";
        String actual = TypeUtils.byteArrayToHexString(fromInteger().getValue());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromHexString) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromHexStringAndValueToHexString() {
        String expected = "6001210121";
        String actual = TypeUtils.byteArrayToHexString(fromHexString().getValue());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CREATING (fromDecimal) & CONVERTING TO HEX-STRING: 6001210121")
    public void assertEqualCreateFromHDecimalAndValueToHexString() {
        String expected = "6001210121";
        String actual = TypeUtils.byteArrayToHexString(fromDecimal().getValue());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("PACKING: 6001210121")
    public void assertEqualPack() throws ISO8583Exception {
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        byte[] actual = fromBytes().pack(charset);
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("UNPACKING: 6001210121")
    public void assertEqualUnPack() throws ISO8583Exception {
        // TPDU: 6001210121
        byte[] expected = new byte[]{0x60, 0x01, 0x21, 0x01, 0x21};

        UnpackContentResult result = fromBytes().unpack(expected, 0, charset);
        byte[] actual = result.getValue();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("UNPACKING: It should throw an exception when the message length is not long enough.")
    public void assertThrowsUnPack() {
        assertThrows(ISO8583Exception.class,
                () -> fromBytes().unpack(new byte[]{0x60, 0x01}, 0, charset));
    }

}
