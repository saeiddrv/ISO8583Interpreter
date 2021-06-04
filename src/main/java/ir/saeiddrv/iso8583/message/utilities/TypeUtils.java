package ir.saeiddrv.iso8583.message.utilities;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;
import java.util.stream.IntStream;

public final class TypeUtils {

    private TypeUtils(){}

    public static String stringToPureNumber(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.replaceAll("[^\\d.]", "");
    }

    public static int[] numberLiteralToIntArray(String numberLiteral) {
        if (numberLiteral == null || numberLiteral.isEmpty()) return new int[]{};
        return Arrays.stream(stringToPureNumber(numberLiteral).split("(?<=.)")).mapToInt(Integer::parseInt).toArray();
    }

    public static int findPreferBCDLength(int length) {
        if (length <= 0) return 0;
        else if (length == 1) return 1;
        else if (length % 2 == 0) return length / 2;
        else return Math.floorDiv(length, 2) + 1;
    }

    public static byte[] textToByteArray(String value) {
        if(value == null || value.isEmpty())
            throw new IllegalArgumentException("Invalid value (convert text to byte array): " + value);

        byte[] bytes = new byte[value.length()];
        for (int i =0; i < value.length(); i++)
            bytes[i] = charToByte(value.charAt(i));
        return bytes;
    }

    public static byte[] amountToByteArray(String value) {
        if(value == null || value.isEmpty())
            throw new IllegalArgumentException("Invalid value (convert amount to byte array): " + value);

        byte[] bytes = new byte[value.length()];
        bytes[0] = charToByte(value.charAt(0));
        for (int i = 1; i < value.length(); i++)
            bytes[i] = (byte) Integer.parseInt(value.substring(i, i+1));
        return bytes;
    }

    public static byte[] numberStringToByteArray(String value) {
        if(value == null || value.isEmpty())
            throw new IllegalArgumentException("Invalid value (convert number to byte array): " + value);

        byte[] bytes = new byte[value.length()];
        for (int i =0; i < value.length(); i++)
            bytes[i] = (byte) Integer.parseInt(value.substring(i, i+1));
        return bytes;
    }

    public static String numberBytesToString(byte[] bytes) {
        if(bytes == null)
            throw new IllegalArgumentException("Invalid value (convert number bytes to string)");

        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) stringBuilder.append(aByte);
        return stringBuilder.toString();
    }

    public static byte charToByte(char character) {
        return (byte) character;
    }

    public static byte charDigitToByte(char character) {
        return (byte) Integer.parseInt(String.valueOf(character));
    }

    public static byte[] bytesToBCD(byte[] bytes) {
        if(bytes == null || bytes.length % 2 != 0)
            throw new IllegalArgumentException("Invalid bytes (convert bytes coding to BCD): "
                    + Objects.requireNonNull(bytes).length);

        int bcdLength = bytes.length / 2;
        byte[] bcdBytes = new byte[bcdLength];
        for (int i = 0, j = 0; i < bcdLength; i++, j += 2)
            bcdBytes[i] = byteToBCD(bytes[j], bytes[j + 1]);
        return bcdBytes;
    }

    public static byte[] decimalToBCD(String decimalValue) {
        if(decimalValue == null || decimalValue.length() % 2 != 0)
            throw new IllegalArgumentException("Invalid decimal (convert decimal coding to BCD byte array): " +
                    Objects.requireNonNull(decimalValue).length());

        int bcdLength = decimalValue.length() / 2;
        byte[] bcdBytes = new byte[bcdLength];
        for (int i = 0, j = 0; i < bcdLength; i++, j += 2)
            bcdBytes[i] = byteToBCD(charDigitToByte(decimalValue.charAt(j)), charDigitToByte(decimalValue.charAt(j + 1)));
        return bcdBytes;
    }

    public static byte byteToBCD(byte low, byte high) {
        return (byte) ((low << 4) | high);
    }

    public static byte[] generateByteArrayFromCharacter(char character, int length, boolean checkNumber) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            if (checkNumber && Character.isDigit(character))
                bytes[i] = (byte) Integer.parseInt(Character.toString(character));
            else
                bytes[i] = charToByte(character);
        return bytes;
    }

    public static String byteToHex(byte value) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((value >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((value & 0xF), 16);
        return new String(hexDigits).toUpperCase();
    }

    public static byte hexToByte(String hexString) {
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
            hexStringBuffer.append(byteToHex(b));
        return hexStringBuffer.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null || hexString.length() % 2 == 1)
            throw new IllegalArgumentException("Invalid hexadecimal (convert hex to byte array): " + hexString);

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2)
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        return bytes;
    }

    public static String bitSetToBinaryString(BitSet bitSet) {
        int size = bitSet.size();
        return IntStream
                .range(0, size)
                .mapToObj(i -> bitSet.get(i) ? '1' : '0')
                .collect(() -> new StringBuilder(size), StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
