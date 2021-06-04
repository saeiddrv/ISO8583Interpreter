package ir.saeiddrv.iso8583.message;

public class ISOMessageException extends Exception {

    public ISOMessageException(String message) {
        super(message);
    }

    public ISOMessageException(String message, Object... args) {
        super(String.format(message, args));
    }
}
