package ir.saeiddrv.iso8583.message;

public class ISOException extends Exception {

    public ISOException(String message) {
        super(message);
    }

    public ISOException(String message, Object... args) {
        super(String.format(message, args));
    }
}
