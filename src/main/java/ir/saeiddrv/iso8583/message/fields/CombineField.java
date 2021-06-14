package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class CombineField implements Field {

    private final int number;
    private final Length length;
    private final Map<Integer, Field> fields = new HashMap<>();
    private Charset charset;
    private ValueFormatter formatter;
    private String description = "UNDEFINED";

    private CombineField(int number,
                         LengthValue lengthValue,
                         LengthInterpreter lengthInterpreter,
                         Field... fields) {
        this.number = number;
        this.length = new Length(lengthValue, lengthInterpreter);
        for (Field field : fields)
            this.fields.put(field.getNumber(), Objects.requireNonNull(field,
                    "The FIELD[" + number + "] can not contains a null subfield."));
    }

    public static CombineField create(int number,
                                      int lengthCount,
                                      int lengthMaximumValue,
                                      LengthInterpreter lengthInterpreter,
                                      Field... fields) {
        return new CombineField(number,
                LengthValue.create(lengthCount, lengthMaximumValue),
                Objects.requireNonNull(lengthInterpreter,
                "LengthInterpreter of FIELD[" + number + "] must not be null"),
                fields);
    }

    public static CombineField create(int number,
                                      LengthValue lengthValue,
                                      LengthInterpreter lengthInterpreter,
                                      Field... fields) {
        return new CombineField(number,
                Objects.requireNonNull(lengthValue,
                        "LengthValue of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(lengthInterpreter,
                        "LengthInterpreter of FIELD[" + number + "] must not be null"),
                fields);
    }

    public static CombineField create(int number, Field... fields) {
        return new CombineField(number, LengthValue.UNDEFINED, null, fields);
    }

    public boolean hasSubField(int fieldNumber) {
        return fields.containsKey(fieldNumber);
    }

    public Field getSubField(int fieldNumber) {
        return fields.get(fieldNumber);
    }

    public int[] getFieldNumbers() {
        return fields.keySet().stream().mapToInt(number -> number).sorted().toArray();
    }

    public int[] rangeOfFieldNumbers(int start, int end) {
        return Arrays.stream(getFieldNumbers()).
                filter(number -> number >= start && number <= end)
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
            field.setFieldNumbers(rangeOfFieldNumbers(bitmapRange.getStart(), bitmapRange.getEnd()));
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
        for (int fieldNumber : getFieldNumbers())
            fields.get(fieldNumber).clear();
    }

    @Override
    public byte[] pack() throws ISO8583Exception {
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
            throw new ISO8583Exception("FIELD[%s]: %s", number, exception.getMessage());
        }
    }

    @Override
    public int unpack(byte[] message, int offset) throws ISO8583Exception {
        try {
            // UNPACK LENGTH
            UnpackLengthResult unpackLength = length.unpack(message, offset, number, charset);
            if (unpackLength != null)
                offset = unpackLength.getNextOffset();

            // UNPACK INNER FIELDS
            for (int fieldNumber : getFieldNumbers())
                offset = fields.get(fieldNumber).unpack(message, offset);

            return offset;

        } catch (Exception exception) {
            throw new ISO8583Exception("FIELD[%s]: %s", number, exception.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder fieldsString = new StringBuilder();
        for (Field field : fields.values())
            fieldsString.append("    ->").append(field.toString()).append("\n");
        return String.format("@CombineField[number: %s, value: %s, fieldCount: %s, charset: %s, description: %s, subFields:\n%s]",
                number,
                hasFormatter() ? getValueFormatted() : getValueAsString(),
                fields.size(),
                charset.displayName(),
                description,
                fieldsString.toString());
    }
}
