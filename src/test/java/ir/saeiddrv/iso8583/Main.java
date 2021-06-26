package ir.saeiddrv.iso8583;

import ir.saeiddrv.iso8583.message.*;
import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.fields.formatters.MaskCardNumber;
import ir.saeiddrv.iso8583.message.fields.shortcuts.*;
import ir.saeiddrv.iso8583.message.headers.TPDU;
import ir.saeiddrv.iso8583.message.interpreters.BCDMTIInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.HexMessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.TPDUHeaderInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.socket.Client;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        try {

            // ==================== DEFINITION ====================

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

            Message message = builder.buildMessage();

            // ==================== SET VALUES TO MESSAGE OBJECT ====================

            System.out.println("\n==================== SET VALUES ====================\n");

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

            message.printObject(System.out);

            System.out.println("\n==================== PACK ====================\n");

            message.setSkipFieldNumbers(37, 39, 38, 54);
            message.printObject(System.out);
            byte[] pack = message.pack();
            System.out.println(TypeUtils.bcdBytesToText(pack));
            System.out.println(TypeUtils.hexDump(pack, StandardCharsets.ISO_8859_1));

            System.out.println("\n==================== UNPACK ====================\n");

            message = builder.unpackMessage(pack);
            message.printObject(System.out);

            System.out.println("\n==================== PACK AGAIN ====================\n");

            byte[] packAgain = message.pack();
            System.out.println(TypeUtils.bcdBytesToText(packAgain));
            System.out.println(TypeUtils.hexDump(packAgain, StandardCharsets.ISO_8859_1));

            System.out.println("\n==================== SEND ====================\n");

            byte[] response = Client.send(packAgain);
            message = builder.unpackMessage(response, System.out);

            System.out.println("\n==================== RESULT: " + message.getValueAsString(39) + " ====================\n");

        } catch (ISO8583Exception ex) {
            ex.printStackTrace();
        }
    }
}
