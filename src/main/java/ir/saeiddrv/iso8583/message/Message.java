package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.fields.*;
import ir.saeiddrv.iso8583.message.interpreters.base.MessageLengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
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

    private boolean isValueOK(Field field, Object value) throws ISO8583Exception {
        if (field instanceof BitmapField)
            throw new ISO8583Exception("The FIELD[%d] is related to 'ISO Bitmap'. " +
                    "The bitmap fields will set automatically.", field.getNumber());
        if (value == null)
            throw new ISO8583Exception("The contents of the field[%d] cannot be set to null. " +
                    "You can use 'ignoreFields' method to ignore this field in pack process and bitmap.", field.getNumber());
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

    private Field findSubField(String fieldNumberSequence) throws ISO8583Exception {
        int[] fieldNumbers = Arrays.stream(fieldNumberSequence.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int sequenceLength = fieldNumbers.length;
        int parentNumber = fieldNumbers[0];

        if (!hasField(parentNumber))
            throw new ISO8583Exception("The FIELD[%d] is not defined.", parentNumber);

        if (sequenceLength == 1) return fields.get(parentNumber);

        if (!isCombineField(parentNumber))
            throw new ISO8583Exception("The FIELD[%d] (parent of %s sequence) is not a CombineField.", parentNumber, fieldNumberSequence);

        CombineField field = (CombineField) fields.get(parentNumber);
        for (int index = 1; index < sequenceLength; index++) {
            int fieldNumber = fieldNumbers[index];
            if (field.hasSubField(fieldNumber)) {
                Field subField = field.getSubField(fieldNumber);
                if (subField instanceof CombineField) field = (CombineField) subField;
                else if (index == sequenceLength - 1) return subField;
                else throw new ISO8583Exception("The FIELD[%d]->FIELD[%d]->[...] (from %s sequence) must be a CombineField.",
                            field.getNumber(), fieldNumber, fieldNumberSequence);
            } else
                throw new ISO8583Exception("The FIELD[%d]->FIELD[%d] (from %s sequence) is not defined.",
                        field.getNumber(), fieldNumber, fieldNumberSequence);
        }
        throw new ISO8583Exception("The end field of %s sequence cannot be a CombineField.", fieldNumberSequence);
    }

    void setBitmaps() {
        for (BitmapField field : getBitmapFields()) {
            Range bitmapRange = field.getBitmap().getRange();
            field.setFieldNumbers(rangeOfFieldNumbers(bitmapRange.getStart(), bitmapRange.getEnd(), true));
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

    public boolean hasLength() {
        return lengthInterpreter != null;
    }

    public void unsetLength() {
        setLengthInterpreter(null);
        lengthCount = 0;
    }

    public int getLengthCount() {
        return lengthCount;
    }

    public void unsetHeader() {
        setHeader(null);
    }

    public Header getHeader() {
        return header;
    }

    public boolean hasHeader() {
        return header != null;
    }

    public void changeMTI(String mtiLiteral) throws ISO8583Exception {
        if (hasMTI()) {
            if (Validator.mti(mtiLiteral)) {
                int[] mtiArray = TypeUtils.numberStringToIntArray(mtiLiteral);
                setMTI(MTI.create(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3], mti.getInterpreter()));
            } else throw new ISO8583Exception("[%s] Is an invalid value for ISO8583 MTI, " +
                        "The message type indicator is a four-digit numeric field " +
                        "which indicates the overall function of the message.", mtiLiteral);
        }
        else throw new ISO8583Exception("The MTI is not defined.");
    }

    public MTI getMti() {
        return mti;
    }

    public boolean hasMTI() {
        return mti != null;
    }

    public void clearMTI() {
        if (hasMTI()) mti.clear();
    }

    public void unsetMTI() {
        mti = null;
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

    public int[] getBitmapFieldNumbers() {
        return fields.values().stream()
                .filter(field -> field instanceof BitmapField)
                .mapToInt(Field::getNumber)
                .toArray();
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

    public int[] rangeOfFieldNumbers(int start, int end, boolean checkIgnores) {
        return Arrays.stream(getFieldNumbers(checkIgnores)).
                filter(number -> number >= start && number <= end)
                .toArray();
    }

    public void setIgnoreFieldNumbers(boolean updateBitmap, int... fieldNumbers) {
        ignoreFields.clear();
        List<Integer> list = Arrays.stream(fieldNumbers).boxed().collect(Collectors.toList());
        ignoreFields.addAll(list);

        if (updateBitmap) setBitmaps();
    }

    public void setIgnoreFieldNumbers(int... fieldNumbers) {
        ignoreFields.clear();
        List<Integer> list = Arrays.stream(fieldNumbers).boxed().collect(Collectors.toList());
        ignoreFields.addAll(list);

        setBitmaps();
    }

    public int[] getIgnoreFieldNumbers() {
        return ignoreFields.stream().mapToInt(number -> number).toArray();
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
        if (isValueOK(fieldNumber, value))
            ((SingleField) fields.get(fieldNumber)).setValue(value);
    }

    public void setValue(int fieldNumber, String value) throws ISO8583Exception {
        if (isValueOK(fieldNumber, value))
            ((SingleField) fields.get(fieldNumber)).setValue(value, charset);
    }

    public void setDeepValue(String fieldNumberSequence, byte[] value) throws ISO8583Exception {
        if (Validator.deepField(fieldNumberSequence)) {
            Field field = findSubField(fieldNumberSequence);
            if (isValueOK(field, value)) ((SingleField) field).setValue(value);
        }
    }

    public void setDeepValue(String fieldNumberSequence, String value) throws ISO8583Exception {
        if (Validator.deepField(fieldNumberSequence)) {
            Field field = findSubField(fieldNumberSequence);
            if (isValueOK(field, value)) ((SingleField) field).setValue(value, charset);
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

    public void clearAllValue(boolean checkIgnores) {
        for(int fieldNumber : getFieldNumbers(checkIgnores))
            fields.get(fieldNumber).clear();
    }

    public void setFields(String[] fieldsValue) throws ISO8583Exception {
        for (int i = 0; i < fieldsValue.length; i++)
            setValue(i, fieldsValue[i]);
    }

    public void setFields(Map<Integer, String> fieldsValue) throws ISO8583Exception {
        for (Map.Entry<Integer, String> entry: fieldsValue.entrySet())
            setValue(entry.getKey(), entry.getValue());
    }

    public byte[] pack() throws ISO8583Exception {
        try {
            // CREATE PACK BUFFER
            ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();

            // PACK HEADER (IF EXIST)
            if (hasHeader())
                messageBuffer.write(header.pack(charset));

            // PACK MTI (IF EXIST)
            if (hasMTI())
                messageBuffer.write(mti.pack(charset));

            // PACK ALL AVAILABLE FIELDS
            for (int fieldNumber : getFieldNumbers(true))
                messageBuffer.write(fields.get(fieldNumber).pack());

            // PACK MESSAGE LENGTH AND MERGE IT
            ByteArrayOutputStream finalPack = new ByteArrayOutputStream();
            byte[] messageBytes =  messageBuffer.toByteArray();
            if (hasLength()) {
                byte[] messageLengthBytes = lengthInterpreter.pack(lengthCount, messageBytes.length, charset);
                finalPack.write(messageLengthBytes);
            }
            finalPack.write(messageBytes);

            // FINISHED
            return finalPack.toByteArray();

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ISO8583Exception("PACKING ERROR: %s", exception.getMessage());
        }
    }

    public Message unpack(byte[] packMessage) throws ISO8583Exception {
        return unpack(packMessage, null);
    }

    public Message unpack(byte[] packMessage, PrintStream printStream) throws ISO8583Exception {
        try {
            printDescription(printStream);

            // INIT OFFSET
            int offset = 0;

            // UNPACK MESSAGE LENGTH (IF EXIST)
            if (hasLength()) {
                UnpackLengthResult unpackMessageLength =
                        lengthInterpreter.unpack(packMessage, offset, lengthCount, charset);
                offset = unpackMessageLength.getNextOffset();
                printLength(printStream);
            }

            // UNPACK HEADER (IF EXIST)
            if (hasHeader()) {
                UnpackContentResult unpackHeader = header.unpack(packMessage, offset, charset);
                offset = unpackHeader.getNextOffset();
                printHeader(printStream);
            }

            // UNPACK MTI (IF EXIST)
            if (hasMTI()) {
                UnpackMTIResult unpackMTI = mti.unpack(packMessage, offset, charset);
                int[] mtiArray = TypeUtils.numberStringToIntArray(unpackMTI.getValue());
                setMTI(MTI.create(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3], mti.getInterpreter()));
                offset = unpackMTI.getNextOffset();
                printMTI(printStream);
            }

            // UNPACK ALL AVAILABLE FIELDS
            BitmapField[] bitmapFields = getBitmapFields();
            for (BitmapField bitmapField : bitmapFields) {
                bitmapField.clear();
                offset = bitmapField.unpack(packMessage, offset);
                printField(bitmapField.getNumber(), printStream);

                for (int fieldNumber : bitmapField.getBitmap().getFiledNumbers()) {
                    if (!hasField(fieldNumber))
                        throw new ISO8583Exception("The FIELD[%d] is not defined.", fieldNumber);

                    Field field = fields.get(fieldNumber);
                    if (field instanceof BitmapField) continue;

                    field.clear();
                    offset = field.unpack(packMessage, offset);
                    printField(fieldNumber, printStream);
                }
            }

            return this;

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ISO8583Exception("UNPACKING ERROR: %s", exception.getMessage());
        }
    }

    public void printDescription(PrintStream printStream) {
        if (printStream != null) printStream.print(logDescription());
    }

    public String logDescription() {
        return "-> DESCRIPTION: " + description + "\n";
    }

    public void printLength(PrintStream printStream) {
        if (printStream != null) printStream.print(logLength());
    }

    public String logLength() {
        StringBuilder builder = new StringBuilder();
        if (hasLength()) {
            String lengthString = String.format(Locale.ENGLISH,
                    "[count: %s, interpreter: %s]", getLengthCount(), lengthInterpreter.getName());
            builder.append("-> LENGTH: ").append(lengthString).append("\n");
        } else builder.append("-> LENGTH: ").append("UNDEFINED").append("\n");
        return builder.toString();
    }

    public void printHeader(PrintStream printStream) {
        if (printStream != null) printStream.print(logHeader());
    }

    public String logHeader() {
        StringBuilder builder = new StringBuilder();
        if (hasHeader()) builder.append("-> HEADER: ").append(header).append("\n");
        else builder.append("-> HEADER: ").append("UNDEFINED").append("\n");
        return builder.toString();
    }

    public void printMTI(PrintStream printStream) {
        if (printStream != null) printStream.print(logMTI());
    }

    public String logMTI() {
        StringBuilder builder = new StringBuilder();
        if (hasMTI()) builder.append("-> MTI   : ").append(mti).append("\n");
        else builder.append("-> MTI   : ").append("UNDEFINED").append("\n");
        return builder.toString();
    }

    public void printFields(PrintStream printStream, boolean checkIgnores) {
        if (printStream != null) printStream.print(logFields(checkIgnores));
    }

    public String logFields(boolean checkIgnores) {
        StringBuilder builder = new StringBuilder();
        for (int fieldNumber : getFieldNumbers(true))
            builder.append(logField(fieldNumber));
        return builder.toString();
    }

    public void printField(int fieldNumber, PrintStream printStream) {
        if (printStream != null) printStream.print(logField(fieldNumber));
    }

    public String logField(int fieldNumber) {
        StringBuilder builder = new StringBuilder();
        if (hasField(fieldNumber))
            builder.append("-> F[").append(String.format(Locale.ENGLISH, "%03d", fieldNumber))
                    .append("]: ").append(fields.get(fieldNumber)).append("\n");
        return builder.toString();
    }

    public void printIgnoreFields(PrintStream printStream) {
        if (printStream != null) printStream.print(logIgnoreFields());
    }

    public String logIgnoreFields() {
        return "-> IGNORE FIELDS: " + Arrays.toString(ignoreFields.toArray()) + "\n";
    }

    public void printObject(PrintStream printStream) {
        if (printStream != null) printStream.println(toString());
    }

    public void printHexDump(PrintStream printStream) throws ISO8583Exception {
        if (printStream != null)
            printStream.println(TypeUtils.hexDump(pack(), charset));
    }

    @Override
    public String toString() {
        return logDescription() +
                logLength() +
                logHeader() +
                logMTI() +
                logFields(false) +
                logIgnoreFields();
    }
}
