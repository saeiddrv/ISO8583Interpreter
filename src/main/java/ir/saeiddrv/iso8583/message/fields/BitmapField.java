package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOMessageException;
import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import ir.saeiddrv.iso8583.message.interpreters.BinaryBitmapInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.base.BitmapInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public class BitmapField implements Field {

    private final int number;
    private final Bitmap bitmap;
    private final BitmapInterpreter interpreter;
    private FieldFormatter formatter = null;
    private String description = "UNDEFINED";

    public static BitmapField createPrimary() {
        return create(FieldNumbers.BITMAP_PRIMARY, BitmapType.PRIMARY, Range.OF_PRIMARY_BITMAP, 8);
    }

    public static BitmapField createPrimary(BitmapInterpreter interpreter) {
        return create(FieldNumbers.BITMAP_PRIMARY, BitmapType.PRIMARY, Range.OF_PRIMARY_BITMAP, 8, interpreter);
    }

    public static BitmapField createSecondary() {
        return create(FieldNumbers.BITMAP_SECONDARY, BitmapType.SECONDARY, Range.OF_SECONDARY_BITMAP, 8);
    }

    public static BitmapField createSecondary(BitmapInterpreter interpreter) {
        return create(FieldNumbers.BITMAP_SECONDARY, BitmapType.SECONDARY, Range.OF_SECONDARY_BITMAP, 8, interpreter);
    }

    public static BitmapField createTertiary() {
        return create(FieldNumbers.TERTIARY_BITMAP, BitmapType.TERTIARY, Range.OF_TERTIARY_BITMAP, 8);
    }

    public static BitmapField createTertiary(BitmapInterpreter interpreter) {
        return create(FieldNumbers.TERTIARY_BITMAP, BitmapType.TERTIARY, Range.OF_TERTIARY_BITMAP, 8, interpreter);
    }

    public static BitmapField create(int number, BitmapType type, Range range, int length) {
        return new BitmapField(number,
                Objects.requireNonNull(type, "BitmapType of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(range, "Range of FIELD[" + number + "] must not be null"),
                length,
                new BinaryBitmapInterpreter());
    }

    public static BitmapField create(int number, BitmapType type, Range range, int length, BitmapInterpreter interpreter) {
        return new BitmapField(number,
                Objects.requireNonNull(type, "BitmapType of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(range, "Range of FIELD[" + number + "] must not be null"),
                length,
                Objects.requireNonNull(interpreter, "BitmapInterpreter of FIELD[" + number + "] must not be null"));
    }

    private BitmapField(int number, BitmapType type, Range range, int length, BitmapInterpreter interpreter) {
        this.number = number;
        this.bitmap = new Bitmap(type, range, length);
        this.interpreter = interpreter;
    }

    public void setFieldNumbers(int[] fieldNumbers) {
        bitmap.setFiledNumbers(fieldNumbers);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public boolean hasFormatter() {
        return formatter != null;
    }

    @Override
    public void setFormatter(FieldFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getFormatted() {
        if (hasFormatter()) return formatter.getFormatted(number, getValue());
        else return null;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return TypeUtils.byteArrayToHexString(getBitmap().getValue());
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void clear() {
        setFieldNumbers(new int[0]);
    }

    @Override
    public byte[] pack(Charset charset) throws IOException, ISOMessageException {
        return interpreter.pack(bitmap, charset);
    }

    @Override
    public String toString() {
        return String.format("@BitmapField[number: %s, value: %s, bitmap: %s, interpreter: %s, description: %s]",
                number, hasFormatter() ? getFormatted() : getValue(), bitmap, interpreter.getName(), description);
    }
}
