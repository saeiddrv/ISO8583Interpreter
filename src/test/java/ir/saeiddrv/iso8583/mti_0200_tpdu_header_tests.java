package ir.saeiddrv.iso8583;

import ir.saeiddrv.iso8583.message.ISO8583;
import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.Message;
import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.fields.BitmapType;
import ir.saeiddrv.iso8583.message.fields.ContentPad;
import ir.saeiddrv.iso8583.message.fields.LengthType;
import ir.saeiddrv.iso8583.message.fields.formatters.MaskCardNumber;
import ir.saeiddrv.iso8583.message.fields.shortcuts.*;
import ir.saeiddrv.iso8583.message.headers.TPDU;
import ir.saeiddrv.iso8583.message.interpreters.BCDMTIInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.HexMessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.TPDUHeaderInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import org.junit.jupiter.api.*;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ISO8583(MTI: 0200, header: TPDU, maxField: 64)")
public class mti_0200_tpdu_header_tests {

    private Message message;

    @BeforeEach
    public void build() throws ISO8583Exception {
        ISO8583 builder = ISO8583.create()
                .setCharset(StandardCharsets.ISO_8859_1)
                .setMessageLengthInterpreter(2, new HexMessageLengthInterpreter())
                .setHeader(TPDU.fromDecimal("60", "121", "121"), new TPDUHeaderInterpreter())
                .setMTI("0200", new BCDMTIInterpreter());

        builder.defineField(0,
                BITMAP.create(BitmapType.PRIMARY, 8, Range.OF_PRIMARY_BITMAP)
                        .setDescription("Bit Map, Primary"));

        builder.defineField(2,
                BCD.create(LengthType.LL, 19, ContentPad.RIGHT_0)
                        .setDescription("Primary Account Number")
                        .setValueFormatter(new MaskCardNumber()));

        builder.defineField(3,
                BCD.create(LengthType.FIXED, 6, ContentPad.LEFT_0)
                        .setDescription("Processing Code"));

        builder.defineField(4,
                BCD.create(LengthType.FIXED, 12, ContentPad.LEFT_0)
                        .setDescription("Amount, Transaction"));

        builder.defineField(11,
                BCD.create(LengthType.FIXED, 6, ContentPad.LEFT_0)
                        .setDescription("System Trace Audit Number"));

        builder.defineField(12,
                BCD.create(LengthType.FIXED, 6, ContentPad.LEFT_0)
                        .setDescription("Time, Local Transaction"));

        builder.defineField(13,
                BCD.create(LengthType.FIXED, 4, ContentPad.LEFT_0)
                        .setDescription("Date, Local Transaction"));

        builder.defineField(24,
                BCD.create(LengthType.FIXED, 3, ContentPad.LEFT_0)
                        .setDescription("Network International Identifier (NII)"));

        builder.defineField(25,
                BCD.create(LengthType.FIXED, 2, ContentPad.LEFT_0)
                        .setDescription("Point of Service Condition Code"));

        builder.defineField(35,
                BCD.create(LengthType.LL, 37, ContentPad.RIGHT_0)
                        .setDescription("Track-2 Data"));

        builder.defineField(37,
                BCD.create(LengthType.FIXED, 12, ContentPad.RIGHT_0)
                        .setDescription("Retrieval Reference Number"));

        builder.defineField(38,
                ASCII.create(LengthType.FIXED, 6, ContentPad.RIGHT_0)
                        .setDescription("Authorization Identification Response"));

        builder.defineField(39,
                ASCII.create(LengthType.FIXED, 2, ContentPad.RIGHT_0)
                        .setDescription("Response Code"));

        builder.defineField(41,
                BCDASCII.create(LengthType.FIXED, 8, ContentPad.LEFT_0)
                        .setDescription("Card Acceptor Terminal Identification"));

        builder.defineField(42,
                BCDASCII.create(LengthType.FIXED, 15, ContentPad.LEFT_0)
                        .setDescription("Card Acceptor Identification Code"));

        builder.defineField(48,
                BCDASCII.create(LengthType.LLL, 999, ContentPad.RIGHT_0)
                        .setDescription("Additional Data"));

        builder.defineField(52,
                BINARY.create(8).setDescription("Personal Identification Number (PIN) Data"));

        builder.defineField(54,
                BCDASCII.create(LengthType.LLL, 999, ContentPad.RIGHT_0)
                        .setDescription("Additional Amounts"));

        builder.defineField(61,
                BCDASCII.create(LengthType.LLL, 999, ContentPad.RIGHT_0)
                        .setDescription("Point of Service (POS) Data"));

        builder.defineField(64,
                BINARY.create(8).setDescription("Message Authentication Code (MAC)"));

        // ==================== BUILD A ISO-8583 MESSAGE OBJECT ====================

        message = builder.buildMessage();

        // ==================== SET VALUES ====================

        message.setValue(2, "6219861026599414");
        message.setValue(3, "000000");
        message.setValue(4, "1000000");
        message.setValue(11, "23");
        message.setValue(12, "144103");
        message.setValue(13, "0531");
        message.setValue(24, "121");
        message.setValue(25, "24");
        message.setValue(35, "6219861026599414=25071004663858811893");
        message.setValue(41, "96090002");
        message.setValue(42, "100273799410001");
        message.setValue(48, "000");
        message.setValue(52, "3E6739B48BFD0A59");
        message.setValue(61, "P260300000066");
        message.setValue(64, "91D941BA6D22AA42");
    }

    @Test
    @Order(1)
    @DisplayName("PACKING")
    public void assertEqualPack() throws ISO8583Exception {
        String expected =
                        "007B" +  // pack[message length], 2 bytes
                        "6001210121" + // pack[tpdu header], 5 bytes
                        "0200" + // pack[mti]
                        "7038018020C11009" + // pack[primary bitmap]
                        "166219861026599414" + // pack[2], pan
                        "000000" + // pack[3], processing code
                        "000001000000" + // pack[4], amount
                        "000023" + // pack[11], stan
                        "144103" + // pack[12], time
                        "0531" + // pack[13], date
                        "0121" + // pack[24], nii
                        "24" + // pack[25], terminal code
                        "376219861026599414D250710046638588118930" + // pack[35], track 2
                        "3936303930303032" + // pack[41], terminal id, 8 bytes
                        "313030323733373939343130303031" +  // pack[42], merchant id, 15 bytes
                        "0003303030" +  // pack[48], additional data
                        "3E6739B48BFD0A59" + // pack[52], pin block, 8 bytes
                        "001350323630333030303030303636" + // pack[61], pos data
                        "91D941BA6D22AA42"; // pack[64], mac, 8 bytes

        // ==================== SKIP some fields from pack process ====================
        message.setSkipFieldNumbers(37, 39, 38, 54);

        // ==================== Packing ====================
        byte[] pack = message.pack();

        // ==================== byte[] -> String ====================
        String actual = TypeUtils.bcdBytesToText(pack);

        assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    @DisplayName("UNPACKING")
    public void assertEqualUnPack() throws ISO8583Exception {
        // ==================== Packing ====================
        byte[] expected = message.pack();

        // ==================== UnPacking ====================
        Message actualMessage = message.unpack(expected);
        byte[] actual = actualMessage.pack();

        assertArrayEquals(expected, actual);
    }
}
