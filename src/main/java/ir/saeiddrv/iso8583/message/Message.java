package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.IntStream;

public class Message {

    Message() {}

    private Charset charset = Charset.defaultCharset();
    private int lengthCount;
    private MessageLengthInterpreter lengthInterpreter;
    private Header header = null;
    private MTI mti = null;
    private String description = "UNDEFINED";
    private final List<Integer> ignoreFields = new ArrayList<>();
    private final Map<Integer, Field> fields = new HashMap<>();

    private boolean isValueOK(int fieldNumber, Object value) throws ISO8583Exception {
        if (!hasField(fieldNumber))
            throw new ISO8583Exception("The FIELD[%d] is not defined.", fieldNumber);
        if (isBitmapField(fieldNumber))
            throw new ISO8583Exception("The FIELD[%d] is related to 'ISO Bitmap'. " +
                    "The bitmap fields will set automatically.", fieldNumber);
        if (value == null)
            throw new ISO8583Exception("The contents of the field[%d] cannot be set to null. " +
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

    void addField(int number, Field field) throws ISO8583Exception {
        if (!fields.containsKey(number)) {
            field.setCharset(charset);
            fields.put(number, field);
        } else throw new ISO8583Exception("The FIELD[%d] is already defined.", number);
    }

    void replaceField(int number, Field field) throws ISO8583Exception {
        if (fields.containsKey(number)) {
            field.setCharset(charset);
            fields.put(number, field);
        } else throw new ISO8583Exception("The FIELD[%d] is not defined.", number);
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

    public void setValue(int fieldNumber, byte[] value) throws ISO8583Exception {
        if (isValueOK(fieldNumber, value)) {
            ((SingleField) fields.get(fieldNumber)).setValue(value);
        }
    }

    public void setValue(int fieldNumber, String value) throws ISO8583Exception {
        if (isValueOK(fieldNumber, value)) {
            ((SingleField) fields.get(fieldNumber)).setValue(value, charset);
        }
    }

    public byte[] getValue(int fieldNumber) {
        if (hasField(fieldNumber))
            return fields.get(fieldNumber).getValue();
        else return null;
    }

    public String getValueAsString(int fieldNumber) {
        if (hasField(fieldNumber))
            return fields.get(fieldNumber).getValueAsString();
        else return null;
    }

    public String getValueFormatted(int fieldNumber) {
        if (hasField(fieldNumber))
            return fields.get(fieldNumber).getValueFormatted();
        else return null;
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

    public byte[] pack() throws ISO8583Exception {
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
                messageBuffer.write(fields.get(fieldNumber).pack());

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
            throw new ISO8583Exception("PACK ERROR: %s", exception.getMessage());
        }
    }

    public Message unpack(byte[] packMessage) throws ISO8583Exception {
        try {
            // INIT OFFSET
            int offset = 0;

            // UNPACK MESSAGE LENGTH (IF EXIST)
            if (lengthInterpreter != null) {
                UnpackLengthResult unpackMessageLength =
                        lengthInterpreter.unpack(packMessage, offset, lengthCount, charset);
                offset = unpackMessageLength.getNextOffset();
                System.out.println("unpackMessageLength: " + unpackMessageLength.getValue());
            }

            // UNPACK HEADER (IF EXIST)
            if (header != null) {
                UnpackContentResult unpackHeader = header.unpack(packMessage, offset, charset);
                offset = unpackHeader.getNextOffset();
                System.out.println("unpackHeader: " + TypeUtils.bcdBytesToText(unpackHeader.getValue()));
            }

            // UNPACK MTI (IF EXIST)
            if (mti != null) {
                UnpackMTIResult unpackMTI = mti.unpack(packMessage, offset, charset);
                int[] mtiArray = TypeUtils.numberStringToIntArray(unpackMTI.getValue());
                setMTI(MTI.create(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3], mti.getInterpreter()));
                offset = unpackMTI.getNextOffset();
                System.out.println("unpackMTI: " + unpackMTI.getValue());
            }

            // UNPACK ALL AVAILABLE FIELDS
            for (int fieldNumber : getFieldNumbers(true)) {
                Field field = fields.get(fieldNumber);
                field.clear();
                offset = field.unpack(packMessage, offset);
                System.out.println(field);
            }

            return this;

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ISO8583Exception("PACK ERROR: %s", exception.getMessage());
        }
    }
}
