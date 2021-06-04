package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.PadDirection;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.ByteBuffer;

public class ContentPad {

    public static final ContentPad NO_PADDING = new ContentPad(PadDirection.NONE, '-');
    public static final ContentPad LEFT_0 = new ContentPad(PadDirection.LEFT, '0');
    public static final ContentPad LEFT_F = new ContentPad(PadDirection.LEFT, 'F');
    public static final ContentPad LEFT_S = new ContentPad(PadDirection.LEFT, ' ');
    public static final ContentPad RIGHT_0 = new ContentPad(PadDirection.RIGHT, '0');
    public static final ContentPad RIGHT_F = new ContentPad(PadDirection.RIGHT, 'F');
    public static final ContentPad RIGHT_S = new ContentPad(PadDirection.RIGHT, ' ');

    private PadDirection padDirection;
    private char character;

    public ContentPad(PadDirection padDirection, char character) {
        this.padDirection = padDirection;
        this.character = character;
    }

    public PadDirection getPadDirection() {
        return padDirection;
    }

    public void setPadDirection(PadDirection padDirection) {
        this.padDirection = padDirection;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public byte[] doPad(byte[] value, ContentType type, int maximumLength) {
        if (value.length >= maximumLength) return value;
        int padLength = maximumLength - value.length;
        boolean checkNumber = (type == ContentType.RAW || type == ContentType.CHARACTER);
        switch (padDirection) {
            case LEFT:
                return ByteBuffer.allocate(value.length + padLength)
                        .put(TypeUtils.generateByteArrayFromCharacter(character, padLength, checkNumber))
                        .put(value)
                        .array();
            case RIGHT:
                return ByteBuffer.allocate(value.length + padLength)
                        .put(value)
                        .put(TypeUtils.generateByteArrayFromCharacter(character, padLength, checkNumber))
                        .array();
            default:
                return value;
        }
    }

    @Override
    public String toString() {
        return String.format("@ContentPad[direction: %s, character: %c]", padDirection, character);
    }
}
