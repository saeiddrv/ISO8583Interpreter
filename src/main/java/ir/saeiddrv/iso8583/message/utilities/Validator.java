package ir.saeiddrv.iso8583.message.utilities;

public final class Validator {

    private Validator() {}

    public static boolean number(String value, int minLength, int maxLength) {
        if (value == null || value.isEmpty()) return false;
        return value.matches("^[0-9]{" + minLength + "," + maxLength +"}$");
    }

    public static boolean number(String value, int length) {
        return number(value, length, length);
    }

    public static boolean mti(String mtiLiteral) {
        return number(mtiLiteral, 4, 4);
    }

    public static boolean hex(String hex, int minLength, int maxLength) {
        if (hex == null || hex.isEmpty()) return false;
        return hex.matches("^[A-Za-z0-9]{" + minLength + "," + maxLength +"}$");
    }

    public static boolean hex(String hex, int length) {
        return hex(hex, length, length);
    }

    public static boolean hex(String hex) {
        if (hex == null || hex.isEmpty()) return false;
        return hex(hex, hex.length(), hex.length());
    }

    public static boolean cardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) return false;
        return number(cardNumber, 16, 19);
    }
}
