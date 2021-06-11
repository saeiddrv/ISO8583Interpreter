package ir.saeiddrv.iso8583.message.utilities;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Locale;
import java.util.stream.IntStream;

public final class TypeUtils {
    private TypeUtils(){}

    public static int findPreferredLengthInBCD(int length) {
        if (length <= 0) return 0;
        else if (length == 1) return 1;
        else if (length % 2 == 0) return length / 2;
        else return Math.floorDiv(length, 2) + 1;
    }

    public static String stringToPureNumber(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.replaceAll("[^\\d.]", "");
    }

    public static int[] numberStringToIntArray(String numberString) {
        if (numberString == null || numberString.isEmpty()) return new int[]{};

        return Arrays
                .stream(stringToPureNumber(numberString).split("(?<=.)"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static String decodeBytes(byte[] bytes, Charset charset) {
        return charset.decode(ByteBuffer.wrap(bytes)).toString();
    }

    public static byte[] encodeBytes(byte[] bytes, Charset charset) {
        return charset.encode(new String(bytes, charset)).array();
    }

    public static byte[] encodeBytes(String value, Charset charset) {
        return charset.encode(value).array();
    }

    public static byte charToByte(char character) {
        return (byte) (int) character;
    }

    public static char byteToChar(byte aByte) {
        return (char) (int) (aByte);
    }

    public static byte[] charSequenceToByteArray(String text) {
        if(text == null || text.isEmpty())
            throw new IllegalArgumentException("Invalid value (convert text to byte array): " + text);

        byte[] bytes = new byte[text.length()];
        for (int i =0; i < text.length(); i++)
            bytes[i] = charToByte(text.charAt(i));
        return bytes;
    }

    public static byte byteToBCD(byte aByte) {
        byte bcd;
        if ((aByte >= '0') && (aByte <= '9')) bcd = (byte) (aByte - '0');
        else if ((aByte >= 'A') && (aByte <= 'F')) bcd = (byte) (aByte - 'A' + 10);
        else if ((aByte >= 'a') && (aByte <= 'f')) bcd = (byte) (aByte - 'a' + 10);
        else bcd = (byte) (aByte - 48);
        return bcd;
    }

    public static byte[] byteArrayToBCD(byte[] bytes) {
        byte[] bcd = new byte[bytes.length / 2];
        int j = 0;
        for (int i = 0; i < (bytes.length + 1) / 2; i++) {
            bcd[i] = byteToBCD(bytes[j++]);
            bcd[i] = (byte) (((j >= bytes.length) ? 0x00 : byteToBCD(bytes[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static String bcdBytesToText(byte[] bytes) {
        char[] temp = new char[bytes.length * 2];
        char val;
        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    public static byte[] generateByteArrayFromCharacter(char character, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = charToByte(character);
        return bytes;
    }

    public static String byteToHexString(byte value) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((value >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((value & 0xF), 16);
        return new String(hexDigits).toUpperCase();
    }

    public static byte hexStringToByte(String hexString) {
        int firstDigit = hexCharToDigit(hexString.charAt(0));
        int secondDigit = hexCharToDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    public static int hexCharToDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1)
            throw new IllegalArgumentException("Invalid Hexadecimal Character (convert hex character to digit): "
                    + hexChar);
        return digit;
    }

    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray)
            hexStringBuffer.append(byteToHexString(b));
        return hexStringBuffer.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null || hexString.length() % 2 == 1)
            throw new IllegalArgumentException("Invalid hexadecimal (convert hex to byte array): " + hexString);

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2)
            bytes[i / 2] = hexStringToByte(hexString.substring(i, i + 2));
        return bytes;
    }

    public static byte[] byteArrayToHexArray(byte[] bytes, Charset charset) {
        return new String(bytes, charset).getBytes(charset);
    }

    public static String bitSetToBinaryString(BitSet bitSet) {
        int size = bitSet.size();
        return IntStream
                .range(0, size)
                .mapToObj(i -> bitSet.get(i) ? '1' : '0')
                .collect(() -> new StringBuilder(size), StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static BitSet byteArray2BitSet(byte[] bytes, int size, int offset) {
        int length = bytes.length << 3;
        BitSet bitSet = new BitSet(size * 8);
        for (int i=0; i< length; i++)
            if ((bytes[i >> 3] & 0x80 >> i % 8) > 0)
                bitSet.set(i);
        return bitSet;
    }

    // Source: https://gist.github.com/jen20/906db194bd97c14d91df
    public static String hexDump(byte[] array, Charset charset) {
        final int width = 16;
        StringBuilder builder = new StringBuilder();

        for (int rowOffset = 0; rowOffset < array.length; rowOffset += width) {
            builder.append(String.format(Locale.ENGLISH, "%06d:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                if (rowOffset + index < array.length)
                    builder.append(String.format("%02X ", array[rowOffset + index]));
                else
                    builder.append("   ");
            }

            int asciiWidth = Math.min(width, array.length - rowOffset);
            builder.append("  |  ");
            builder.append(new String(array, rowOffset, asciiWidth, charset)
                    .replaceAll("\r\n", " ")
                    .replaceAll("\n", " "));

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }
}
