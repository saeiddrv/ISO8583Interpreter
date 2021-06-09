package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISOException;
import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class CombineField implements Field {

    private final int number;
    private final Length length;
    private final Map<Integer, Field> fields = new HashMap<>();
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    public CombineField(int number, Length length, Field... fields) {
        this.number = number;
        this.length = length;
        for (Field field : fields)
            this.fields.put(field.getNumber(), field);
    }

    public Field getSubField(int fieldNumber) {
        return fields.get(fieldNumber);
    }

    public int[] getFieldNumbers() {
        return fields.keySet().stream().mapToInt(number -> number).sorted().toArray();
    }

    public int[] selectFieldNumbers(int from, int to) {
        return Arrays.stream(getFieldNumbers()).
                filter(number -> number >= from && number <= to)
                .toArray();
    }

    public BitmapField[] getBitmapFields() {
        return fields.values().stream()
                .filter(field -> field instanceof BitmapField)
                .map(field -> (BitmapField) field)
                .toArray(BitmapField[]::new);
    }

    public SingleField[] getSingleFields() {
        return fields.values().stream()
                .filter(field -> field instanceof SingleField)
                .map(field -> (SingleField) field)
                .toArray(SingleField[]::new);
    }

    private void setBitmaps() {
        for (BitmapField field : getBitmapFields()) {
            Range bitmapRange = field.getBitmap().getRange();
            field.setFieldNumbers(selectFieldNumbers(bitmapRange.getStart(), bitmapRange.getEnd()));
        }
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
    public void setCharset(Charset charset) {
        if(this.charset == null) this.charset = charset;
    }

    @Override
    public void setValueFormatter(ValueFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getValueFormatted() {
        if (hasFormatter()) return formatter.getFormatted(number, getValueAsString());
        else return null;
    }

    @Override
    public void setDescription(String description) {
        this.description =description;
    }

    @Override
    public byte[] getValue() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (int fieldNumber : getFieldNumbers())
                buffer.write(fields.get(fieldNumber).getValue());
            return buffer.toByteArray();

        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public String getValueAsString() {
        StringBuilder builder = new StringBuilder();
        for (int fieldNumber : getFieldNumbers())
            builder.append(fields.get(fieldNumber).getValueAsString());
        return builder.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void clear() {
        fields.clear();
    }

    @Override
    public byte[] pack() throws ISOException {
        try {
            setBitmaps();
            // START FIELD PACKING, CREATE PACK BUFFER
            ByteArrayOutputStream finalBuffer = new ByteArrayOutputStream();

            // PACK ALL AVAILABLE FIELDS
            ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
            for (int fieldNumber : getFieldNumbers())
                contentBuffer.write(fields.get(fieldNumber).pack());
            byte[] contentPack = contentBuffer.toByteArray();

            finalBuffer.write(length.pack(number, contentPack.length, charset));
            finalBuffer.write(contentPack);

            // FINISHED
            return finalBuffer.toByteArray();

        } catch (Exception exception) {
            throw new ISOException("FIELD[%s]: %s", number, exception.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Field field : fields.values())
            stringBuilder.append("    ->").append(field.toString()).append("\n");
        return String.format("@CombineField[number: %s, value: %s, fieldCount: %s, charset: %s, description: %s, subFields:\n%s]",
                number,
                hasFormatter() ? getValueFormatted() : getValueAsString(),
                fields.size(),
                charset.displayName(),
                description,
                stringBuilder.toString());
    }
}
