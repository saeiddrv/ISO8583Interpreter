package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.IntStream;

public class ISOMessage {

    ISOMessage() {}

    private Charset charset = Charset.defaultCharset();
    private int lengthCount;
    private MessageLengthInterpreter lengthInterpreter;
    private Header header = null;
    private MTI mti = null;
    private String description = "UNDEFINED";
    private final List<Integer> ignoreFields = new ArrayList<>();
    private final Map<Integer, Field> fields = new HashMap<>();

    private boolean isValueOK(int fieldNumber, Object value) throws ISOMessageException {
        if (!hasField(fieldNumber))
            throw new ISOMessageException("The FIELD[%d] is not defined.", fieldNumber);
        if (isBitmapField(fieldNumber))
            throw new ISOMessageException("The FIELD[%d] is related to 'ISO Bitmap'. " +
                    "The bitmap fields will set automatically.", fieldNumber);
        if (value == null)
            throw new ISOMessageException("The contents of the field[%d] cannot be set to null. " +
                    "You can use 'ignoreFields' method to ignore this field in pack process and bitmap.", fieldNumber);
        return true;
    }

    private BitmapField getBitmapField(BitmapType type) {
        for (BitmapField field : getBitmapFields())
            if (field.getBitmap().getType() == type) return field;
        return null;
    }

    private Bitmap getBitmap(BitmapType type) {
        BitmapField field = getBitmapField(type);
        if (field != null) return field.getBitmap();
        else return null;
    }

    void setBitmaps() {
        for (BitmapField field : getBitmapFields()) {
            Range bitmapRange = field.getBitmap().getRange();
            field.setFieldNumbers(selectFieldNumbers(bitmapRange.getStart(), bitmapRange.getEnd(), true));
        }
    }

    void setDescription(String description) {
        this.description  = description;
    }

    void setLengthCount(int lengthCount) {
        this.lengthCount = lengthCount;
    }

    void setCharset (Charset charset) {
        this.charset = charset;
    }

    void setLengthInterpreter(MessageLengthInterpreter lengthInterpreter) {
        this.lengthInterpreter = lengthInterpreter;
    }

    void setHeader(Header header) {
        this.header = header;
    }

    void setMTI(MTI mti) {
        this.mti = mti;
    }

    void addField(int number, Field field) throws ISOMessageException {
        if (!fields.containsKey(number)) {
            fields.put(number, field);
        } else throw new ISOMessageException("The FIELD[%d] is already defined.", number);
    }

    void replaceField(int number, Field field) throws ISOMessageException {
        if (fields.containsKey(number)) {
            fields.put(number, field);
        } else throw new ISOMessageException("The FIELD[%d] is not defined.", number);
    }

    public Charset getCharset() {
        return charset;
    }

    public MTI getMti() {
        return mti;
    }

    public void unsetHeader() {
        setHeader(null);
    }

    public Header getHeader() {
        return header;
    }

    public Field[] getFields() {
        return fields.values().toArray(new Field[0]);
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

    public CombineField[] getCombineFields() {
        return fields.values().stream()
                .filter(field -> field instanceof CombineField)
                .map(field -> (CombineField) field)
                .toArray(CombineField[]::new);
    }

    public int[] getFieldNumbers(boolean checkIgnores) {
        IntStream stream = fields.keySet().stream().mapToInt(number -> number);
        if (checkIgnores) stream = stream.filter(number -> !ignoreFields.contains(number));
        return stream.sorted().toArray();
    }

    public int[] selectFieldNumbers(int from, int to, boolean checkIgnores) {
        return Arrays.stream(getFieldNumbers(checkIgnores)).
                filter(number -> number >= from && number <= to)
                .toArray();
    }

    public void ignoreFields(Integer... fieldNumbers) {
        ignoreFields.clear();
        Collections.addAll(ignoreFields, fieldNumbers);
        setBitmaps();
    }

    public void clearIgnoreFields() {
        ignoreFields.clear();
        setBitmaps();
    }

    public Bitmap getPrimaryBitmap() {
        return getBitmap(BitmapType.PRIMARY);
    }

    public Bitmap getSecondaryBitmap() {
        return getBitmap(BitmapType.SECONDARY);
    }

    public Bitmap getTertiaryBitmap() {
        return getBitmap(BitmapType.TERTIARY);
    }

    public boolean hasField(int fieldNumber) {
        return fields.containsKey(fieldNumber);
    }

    public Field getField(int fieldNumber) {
        return fields.get(fieldNumber);
    }

    public boolean isBitmapField(int fieldNumber) {
        return fields.get(fieldNumber) instanceof BitmapField;
    }

    public boolean isSingleField(int fieldNumber) {
        return fields.get(fieldNumber) instanceof SingleField;
    }

    public boolean isCombineField(int fieldNumber) {
        return fields.get(fieldNumber) instanceof CombineField;
    }

    public int getMinimumFieldNumber(boolean checkIgnores) {
        int[] fieldNumbers = getFieldNumbers(checkIgnores);
        if (fieldNumbers.length > 0) return fieldNumbers[0];
        else return 0;
    }

    public int getMaximumFieldNumber(boolean checkIgnores) {
        int[] fieldNumbers = getFieldNumbers(checkIgnores);
        if (fieldNumbers.length > 0) return fieldNumbers[fieldNumbers.length - 1];
        else return 0;
    }

    public void setValue(int fieldNumber, byte[] value) throws ISOMessageException {
        if (isValueOK(fieldNumber, value)) {
            ((SingleField) fields.get(fieldNumber)).setValue(value);
        }
    }

    public void setValue(int fieldNumber, String value) throws ISOMessageException {
        if (isValueOK(fieldNumber, value)) {
            ((SingleField) fields.get(fieldNumber)).setValue(value);
        }
    }

    public void clearValue(int fieldNumber) {
        if (hasField(fieldNumber))
            fields.get(fieldNumber).clear();
    }

    public void printObject(PrintStream printStream) {
        StringBuilder builder = new StringBuilder();
        builder.append("-> DESCRIPTION: ").append(description).append("\n");
        if (mti != null) builder.append("-> HEADER: ").append(header).append("\n");
        else builder.append("-> HEADER: ").append("UNDEFINED").append("\n");
        if (mti != null) builder.append("-> MTI: ").append(mti).append("\n");
        else builder.append("-> MTI: ").append("UNDEFINED").append("\n");
        for (int fieldNumber : getFieldNumbers(false))
            builder.append("-> F[").append(String.format(Locale.ENGLISH, "%03d", fieldNumber))
                    .append("]: ").append(fields.get(fieldNumber)).append("\n");
        printStream.println(builder.toString());
    }

    public byte[] pack() throws ISOMessageException {
        try {
            // CREATE PACK BUFFER
            ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();

            // PACK HEADER (IF EXIST)
            if (header != null)
                messageBuffer.write(header.pack(charset));

            // PACK MTI (IF EXIST)
            if (mti != null)
                messageBuffer.write(mti.pack(charset));

            // PACK ALL AVAILABLE FIELDS
            for (int fieldNumber : getFieldNumbers(true))
                messageBuffer.write(fields.get(fieldNumber).pack(charset));

            // PACK MESSAGE LENGTH AND MERGE IT
            ByteArrayOutputStream finalPack = new ByteArrayOutputStream();
            byte[] messageBytes =  messageBuffer.toByteArray();
            if (lengthInterpreter != null) {
                byte[] messageLengthBytes = lengthInterpreter.pack(lengthCount, messageBytes.length, charset);
                finalPack.write(messageLengthBytes);
            }
            finalPack.write(messageBytes);

            // FINISHED
            return finalPack.toByteArray();

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ISOMessageException("PACK ERROR (%s)", exception.getMessage());
        }
    }
}
