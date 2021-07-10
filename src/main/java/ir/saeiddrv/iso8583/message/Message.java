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

/**
 * This class provide an ISO-8583 Message object.
 *
 * @author Saeid Darvish
 */
public class Message {

    Message() {}

    private Charset charset = Charset.defaultCharset();
    private int lengthCount;
    private MessageLengthInterpreter lengthInterpreter;
    private Header header = null;
    private MTI mti = null;
    private String description = "UNDEFINED";
    private final List<Integer> skipFields = new ArrayList<>();
    private final Map<Integer, Field> fields = new HashMap<>();

    private boolean isValueOK(int fieldNumber, Object value) throws ISO8583Exception {
        if (!hasField(fieldNumber))
            throw new ISO8583Exception("The FIELD[%d] is not defined.", fieldNumber);
        if (isBitmapField(fieldNumber))
            throw new ISO8583Exception("The FIELD[%d] is related to 'ISO Bitmap'. " +
                    "The bitmap fields will set automatically.", fieldNumber);
        if (value == null)
            throw new ISO8583Exception("The content of the FIELD[%d] cannot be set to null. " +
                    "You can use 'setSkipFields' method to skip from this field in pack process and bitmap generation.", fieldNumber);
        return true;
    }

    private boolean isValueOK(Field field, Object value) throws ISO8583Exception {
        if (field instanceof BitmapField)
            throw new ISO8583Exception("The FIELD[%d] is related to 'ISO Bitmap'. " +
                    "The bitmap fields will set automatically.", field.getNumber());
        if (value == null)
            throw new ISO8583Exception("The content of the FIELD[%d] cannot be set to null. " +
                    "You can use 'setSkipFields' method to skip from this field in pack process and bitmap generation.", field.getNumber());
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

    private void setSkipFieldsInUnpack(List<Integer> skipFields) {
        int[] differences = IntStream.of(getFieldNumbers(false))
                .filter(element -> !skipFields.contains(element))
                .toArray();
        setSkipFieldNumbers(differences);
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

    /**
     * current charset of the message object
     *
     * @return message charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Check if an interpreter for message length is exist
     *
     * @return true if an interpreter for message length has been defined
     */
    public boolean hasLength() {
        return lengthInterpreter != null;
    }

    /**
     * Remove the interpreter for message length
     */
    public void unsetLength() {
        setLengthInterpreter(null);
        lengthCount = 0;
    }

    /**
     * Number of message length digits
     *
     * @return the number of message length digits
     */
    public int getLengthCount() {
        return lengthCount;
    }

    /**
     * Remove header from the message
     */
    public void unsetHeader() {
        setHeader(null);
    }

    /**
     * Header of the message
     *
     * @return a Header object
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Check if a header for message is exist
     *
     * @return true if a header for message has been defined
     */
    public boolean hasHeader() {
        return header != null;
    }

    /**
     * Change the current MTI value
     *
     * @param mtiLiteral a MTI numeric string. example: "0200"
     * @throws ISO8583Exception if input has been invalid or MTI is not defined
     */
    public void changeMTI(String mtiLiteral) throws ISO8583Exception {
        if (hasMTI()) {
            if (Validator.mti(mtiLiteral)) {
                int[] mtiArray = TypeUtils.numberStringToIntArray(mtiLiteral);
                setMTI(new MTI(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3], mti.getInterpreter()));
            } else throw new ISO8583Exception("[%s] Is an invalid value for ISO8583 MTI, " +
                        "The message type indicator is a four-digit numeric field " +
                        "which indicates the overall function of the message.", mtiLiteral);
        }
        else throw new ISO8583Exception("The MTI is not defined.");
    }

    /**
     * MTI of the message
     *
     * @return MTI object of the message
     */
    public MTI getMti() {
        return mti;
    }

    /**
     * Check if a MTI for message is exist
     *
     * @return true if a MTI object for message has been defined
     */
    public boolean hasMTI() {
        return mti != null;
    }

    /**
     * Clear the MTI value
     */
    public void clearMTI() {
        if (hasMTI()) mti.clear();
    }

    /**
     * remove the MTI value from the message
     */
    public void unsetMTI() {
        mti = null;
    }

    /**
     * The fields defined in this message
     *
     * @return an array of Field objects
     */
    public Field[] getFields() {
        return fields.values().toArray(new Field[0]);
    }

    /**
     * The fields of bitmap type (BitmapField object)
     *
     * @return an array of BitmapField objects
     */
    public BitmapField[] getBitmapFields() {
        return fields.values().stream()
                .filter(field -> field instanceof BitmapField)
                .map(field -> (BitmapField) field)
                .sorted(Comparator.comparingInt(BitmapField::getNumber))
                .toArray(BitmapField[]::new);
    }

    /**
     * The fields of bitmap type (BitmapField object) numbers
     *
     * @return an array of BitmapField indexes
     */
    public int[] getBitmapFieldNumbers() {
        return fields.values().stream()
                .filter(field -> field instanceof BitmapField)
                .mapToInt(Field::getNumber)
                .toArray();
    }

    /**
     * The fields of single type (SingleField object)
     *
     * @return an array of SingleField objects
     */
    public SingleField[] getSingleFields() {
        return fields.values().stream()
                .filter(field -> field instanceof SingleField)
                .map(field -> (SingleField) field)
                .sorted(Comparator.comparingInt(SingleField::getNumber))
                .toArray(SingleField[]::new);
    }

    /**
     * The fields of combination type (CombineField object)
     *
     * @return an array of CombineField objects
     */
    public CombineField[] getCombineFields() {
        return fields.values().stream()
                .filter(field -> field instanceof CombineField)
                .map(field -> (CombineField) field)
                .sorted(Comparator.comparingInt(CombineField::getNumber))
                .toArray(CombineField[]::new);
    }

    public int[] getFieldNumbers(boolean doSkipping) {
        IntStream stream = fields.keySet().stream().mapToInt(number -> number);
        if (doSkipping) stream = stream.filter(number -> !skipFields.contains(number));
        return stream.sorted().toArray();
    }

    public int[] rangeOfFieldNumbers(int start, int end, boolean doSkipping) {
        return Arrays.stream(getFieldNumbers(doSkipping)).
                filter(number -> number >= start && number <= end)
                .toArray();
    }

    public void setSkipFieldNumbers(boolean updateBitmap, int... fieldNumbers) {
        skipFields.clear();
        List<Integer> list = Arrays.stream(fieldNumbers).boxed().collect(Collectors.toList());
        skipFields.addAll(list);

        if (updateBitmap) setBitmaps();
    }

    public void setSkipFieldNumbers(int... fieldNumbers) {
        skipFields.clear();
        List<Integer> list = Arrays.stream(fieldNumbers).boxed().collect(Collectors.toList());
        skipFields.addAll(list);

        setBitmaps();
    }

    public int[] getSkipFieldNumbers() {
        return skipFields.stream().mapToInt(number -> number).toArray();
    }

    public void clearSkipFields() {
        skipFields.clear();
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

    public int getMinimumFieldNumber(boolean doSkipping) {
        int[] fieldNumbers = getFieldNumbers(doSkipping);
        if (fieldNumbers.length > 0) return fieldNumbers[0];
        else return 0;
    }

    public int getMaximumFieldNumber(boolean doSkipping) {
        int[] fieldNumbers = getFieldNumbers(doSkipping);
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

    public void clearAllValue(boolean doSkipping) {
        for(int fieldNumber : getFieldNumbers(doSkipping))
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

    // Packing

    public byte[] pack() throws ISO8583Exception {
        try {
            // CREATE PACK BUFFER
            ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();

            // PACKING HEADER (IF EXIST)
            if (hasHeader())
                messageBuffer.write(header.pack(charset));

            // PACKING MTI (IF EXIST)
            if (hasMTI())
                messageBuffer.write(mti.pack(charset));

            // PACKING ALL AVAILABLE FIELDS
            for (int fieldNumber : getFieldNumbers(true))
                messageBuffer.write(fields.get(fieldNumber).pack());

            // PACKING MESSAGE LENGTH AND MERGE IT
            ByteArrayOutputStream finalPack = new ByteArrayOutputStream();
            byte[] messageBytes =  messageBuffer.toByteArray();
            if (hasLength()) {
                byte[] messageLengthBytes = lengthInterpreter.pack(lengthCount, messageBytes.length, charset);
                finalPack.write(messageLengthBytes);
            }
            finalPack.write(messageBytes);

            // FINISH
            return finalPack.toByteArray();

        } catch (Exception exception) {
            throw new ISO8583Exception("PACKING ERROR: %s", exception.getMessage());
        }
    }

    // Unpacking

    public Message unpack(byte[] packMessage) throws ISO8583Exception {
        return unpack(packMessage, null);
    }

    public Message unpack(byte[] packMessage, PrintStream printStream) throws ISO8583Exception {
        try {
            printDescription(printStream);

            // INIT OFFSET
            int offset = 0;

            // UNPACKING MESSAGE LENGTH (IF EXIST)
            if (hasLength()) {
                UnpackLengthResult unpackMessageLength =
                        lengthInterpreter.unpack(packMessage, offset, lengthCount, charset);
                offset = unpackMessageLength.getNextOffset();
                printLength(printStream);
            }

            // UNPACKING HEADER (IF EXIST)
            if (hasHeader()) {
                UnpackContentResult unpackHeader = header.unpack(packMessage, offset, charset);
                offset = unpackHeader.getNextOffset();
                printHeader(printStream);
            }

            // UNPACKING MTI (IF EXIST)
            if (hasMTI()) {
                UnpackMTIResult unpackMTI = mti.unpack(packMessage, offset, charset);
                int[] mtiArray = TypeUtils.numberStringToIntArray(unpackMTI.getValue());
                setMTI(new MTI(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3], mti.getInterpreter()));
                offset = unpackMTI.getNextOffset();
                printMTI(printStream);
            }

            // UNPACKING ALL AVAILABLE FIELDS
            List<Integer> fieldNumbers = new ArrayList<>();

            BitmapField[] bitmapFields = getBitmapFields();
            for (BitmapField bitmapField : bitmapFields) {
                bitmapField.clear();
                offset = bitmapField.unpack(packMessage, offset);
                fieldNumbers.add(bitmapField.getNumber());
                printField(bitmapField.getNumber(), printStream);

                for (int fieldNumber : bitmapField.getBitmap().getFiledNumbers()) {
                    if (!hasField(fieldNumber))
                        throw new ISO8583Exception("The FIELD[%d] is not defined.", fieldNumber);

                    Field field = fields.get(fieldNumber);
                    if (field instanceof BitmapField) continue;

                    field.clear();
                    offset = field.unpack(packMessage, offset);
                    fieldNumbers.add(fieldNumber);
                    printField(fieldNumber, printStream);
                }
            }

            setSkipFieldsInUnpack(fieldNumbers);
            printSkipFields(printStream);

            // FINISH
            return this;

        } catch (Exception exception) {
            throw new ISO8583Exception("UNPACKING ERROR: %s", exception.getMessage());
        }
    }

    // Print objects to output

    public void printDescription(PrintStream printStream) {
        if (printStream != null) printStream.print(descriptionToString());
    }

    public void printLength(PrintStream printStream) {
        if (printStream != null) printStream.print(lengthToString());
    }

    public void printHeader(PrintStream printStream) {
        if (printStream != null) printStream.print(headerToString());
    }

    public void printMTI(PrintStream printStream) {
        if (printStream != null) printStream.print(mtiToString());
    }

    public void printFields(PrintStream printStream, boolean doSkipping) {
        if (printStream != null) printStream.print(fieldsToString(doSkipping));
    }

    public void printField(int fieldNumber, PrintStream printStream) {
        if (printStream != null) printStream.print(fieldToString(fieldNumber));
    }

    public void printSkipFields(PrintStream printStream) {
        if (printStream != null) printStream.print(skipFieldsToString());
    }

    /**
     * Send String representation of the Message object to a specific output.
     *
     * @param printStream the intended output: <code>System.out</code>, <code>File</code>, ...
     */
    public void printObject(PrintStream printStream) {
        if (printStream != null) printStream.println(toString());
    }

    /**
     * Send hexdump of the message to a specific output.
     *
     * @param printStream the intended output: <code>System.out</code>, <code>File</code>, ...
     * @throws ISO8583Exception if pack process throws it
     */
    public void printHexDump(PrintStream printStream) throws ISO8583Exception {
        if (printStream != null)
            printStream.println(TypeUtils.hexDump(pack(), charset));
    }

    /**
     * Convert message description to log format.
     *
     * @return a representation of the message description in log format.
     */
    public String descriptionToString() {
        return "-> DESCRIPTION: " + description + "\n";
    }

    /**
     * Convert length of the message object to String in log format.
     *
     * @return a string representation of the length of the message object in log format.
     */
    public String lengthToString() {
        StringBuilder builder = new StringBuilder();
        if (hasLength()) {
            String lengthString = String.format(Locale.ENGLISH,
                    "[count: %s, interpreter: %s]", getLengthCount(), lengthInterpreter.getName());
            builder.append("-> LENGTH: ").append(lengthString).append("\n");
        } else builder.append("-> LENGTH: ").append("UNDEFINED").append("\n");
        return builder.toString();
    }

    /**
     * Convert header object to String in log format.
     *
     * @return a string representation of the <code>Header</code> object in log format.
     */
    public String headerToString() {
        StringBuilder builder = new StringBuilder();
        if (hasHeader()) builder.append("-> HEADER: ").append(header).append("\n");
        else builder.append("-> HEADER: ").append("UNDEFINED").append("\n");
        return builder.toString();
    }

    /**
     * Convert MTI object to String in log format.
     *
     * @return a string representation of the <code>MTI</code> object in log format.
     */
    public String mtiToString() {
        StringBuilder builder = new StringBuilder();
        if (hasMTI()) builder.append("-> MTI   : ").append(mti).append("\n");
        else builder.append("-> MTI   : ").append("UNDEFINED").append("\n");
        return builder.toString();
    }

    /**
     * Convert all Field objects to String in log format.
     *
     * @param doSkipping <code>true</code>: skipping fields will not become
     * @return a string representation of the all <code>Field</code> objects in log format.
     *         each field will be present in a separate line.
     */
    public String fieldsToString(boolean doSkipping) {
        StringBuilder builder = new StringBuilder();
        for (int fieldNumber : getFieldNumbers(doSkipping))
            builder.append(fieldToString(fieldNumber));
        return builder.toString();
    }

    /**
     * Convert Field object to String in log format.
     *
     * @param fieldNumber the intended field number
     * @return a string representation of the a specific <code>Field</code> object in log format.
     */
    public String fieldToString(int fieldNumber) {
        StringBuilder builder = new StringBuilder();
        if (hasField(fieldNumber))
            builder.append("-> F[")
                    .append(String.format(Locale.ENGLISH, "%03d", fieldNumber))
                    .append("]: ")
                    .append(fields.get(fieldNumber))
                    .append("\n");
        return builder.toString();
    }

    /**
     * Convert array of the skipping fields to String in log format.
     *
     * @return a string representation of the skipping fields in log format.
     */
    public String skipFieldsToString() {
        return "-> SKIPPING FIELDS: " + Arrays.toString(skipFields.toArray()) + "\n";
    }

    /**
     * Convert Message object to String in log format.
     *
     * @return a string representation of the <code>Message</code> object in log format.
     */
    @Override
    public String toString() {
        return descriptionToString() +
                lengthToString() +
                headerToString() +
                mtiToString() +
                fieldsToString(false) +
                skipFieldsToString();
    }
}
