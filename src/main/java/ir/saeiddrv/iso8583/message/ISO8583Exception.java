package ir.saeiddrv.iso8583.message;

public class ISO8583Exception extends Exception {

    public ISO8583Exception(String message) {
        super(message);
    }

    public ISO8583Exception(String message, Object... args) {
        super(String.format(message, args));
    }
}
