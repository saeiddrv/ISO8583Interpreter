package ir.saeiddrv.iso8583.message.utilities;

public final class PadUtils {

    private PadUtils() {}

    public static String padLeft(String value, int length, char character) {
        if (value.length() >= length) return value;

        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - value.length()) sb.append(character);
        sb.append(value);

        return sb.toString();
    }

    public static String padRight(String value, int length, char character) {
        if (value.length() >= length) return value;

        StringBuilder sb = new StringBuilder();
        sb.append(value);
        while (sb.length() < length - value.length()) sb.append(character);

        return sb.toString();
    }

}
