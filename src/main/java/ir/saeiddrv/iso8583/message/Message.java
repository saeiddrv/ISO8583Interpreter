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
     * Get current charset of this message
     *
     * @return message charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Check if an interpreter for length of this message has been defined
     *
     * @return true if an interpreter for length of this message has been defined
     */
    public boolean hasLength() {
        return lengthInterpreter != null;
    }

    /**
     * Remove the interpreter for length of this message
     */
    public void unsetLength() {
        setLengthInterpreter(null);
        lengthCount = 0;
    }

    /**
     * Get number of length digits of this message
     *
     * @return the number of length digits of this message
     */
    public int getLengthCount() {
        return lengthCount;
    }

    /**
     * Remove the header from this message
     */
    public void unsetHeader() {
        setHeader(null);
    }

    /**
     * Get the header of this message, if defined
     *
     * @return header of this message
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Check if a header for this message has been defined
     *
     * @return true if a header for this message has been defined
     */
    public boolean hasHeader() {
        return header != null;
    }

    /**
     * Change the current MTI value in this message
     *
     * @param mtiLiteral a MTI numeric string. example: "0200"
     * @throws ISO8583Exception if input has been invalid or MTI for this message has not been defined
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
        else throw new ISO8583Exception("The MTI has not been defined.");
    }

    /**
     * Get the MTI of this message
     *
     * @return MTI object of this message
     */
    public MTI getMti() {
        return mti;
    }

    /**
     * Check if a MTI for ths message has been defined
     *
     * @return true if a MTI for this message has been defined
     */
    public boolean hasMTI() {
        return mti != null;
    }

    /**
     * Clear the MTI value from this message
     */
    public void clearMTI() {
        if (hasMTI()) mti.clear();
    }

    /**
     * Remove the MTI value from this message
     */
    public void unsetMTI() {
        mti = null;
    }

    /**
     * Get the fields defined in this message
     *
     * @return an array of the fields (Field object) defined in this message
     */
    public Field[] getFields() {
        return fields.values().toArray(new Field[0]);
    }

    /**
     * Get the fields of bitmap type (BitmapField object) defined in this message
     *
     * @return an array of BitmapField type
     */
    public BitmapField[] getBitmapFields() {
        return fields.values().stream()
                .filter(field -> field instanceof BitmapField)
                .map(field -> (BitmapField) field)
                .sorted(Comparator.comparingInt(BitmapField::getNumber))
                .toArray(BitmapField[]::new);
    }

    /**
     * Get the fields of bitmap type (BitmapField object) numbers
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
     * Get the fields of single type (SingleField object) defined in this message
     *
     * @return an array of SingleField type
     */
    public SingleField[] getSingleFields() {
        return fields.values().stream()
                .filter(field -> field instanceof SingleField)
                .map(field -> (SingleField) field)
                .sorted(Comparator.comparingInt(SingleField::getNumber))
                .toArray(SingleField[]::new);
    }

    /**
     * Get the fields of combination type (CombineField object) defined in this message
     *
     * @return an array of CombineField type
     */
    public CombineField[] getCombineFields() {
        return fields.values().stream()
                .filter(field -> field instanceof CombineField)
                .map(field -> (CombineField) field)
                .sorted(Comparator.comparingInt(CombineField::getNumber))
                .toArray(CombineField[]::new);
    }

    /**
     * Get the index of fields defined in this message
     *
     * @param doSkipping if true, then only the index of fields that exist in bitmap will be returned.
     * @return an array of field indexes
     */
    public int[] getFieldNumbers(boolean doSkipping) {
        IntStream stream = fields.keySet().stream().mapToInt(number -> number);
        if (doSkipping) stream = stream.filter(number -> !skipFields.contains(number));
        return stream.sorted().toArray();
    }

    /**
     * Get the index of fields defined in this message based a range
     *
     * @param doSkipping if true, then only the index of fields that exist in bitmap will be returned.
     * @return an array of field indexes
     */
    public int[] rangeOfFieldNumbers(int start, int end, boolean doSkipping) {
        return Arrays.stream(getFieldNumbers(doSkipping)).
                filter(number -> number >= start && number <= end)
                .toArray();
    }

    /**
     * Set the index of fields that must be skipped from the bitmap generation and pack processing
     *
     * @param updateBitmap if true, then regenerate bitmap
     * @param fieldNumbers the index of fields that must be skipped, as a vararg
     */
    public void setSkipFieldNumbers(boolean updateBitmap, int... fieldNumbers) {
        skipFields.clear();
        List<Integer> list = Arrays.stream(fieldNumbers).boxed().collect(Collectors.toList());
        skipFields.addAll(list);

        if (updateBitmap) setBitmaps();
    }

    /**
     * Set the index of fields that must be skipped from the bitmap generation and pack processing
     * Also, the bitmap will be regenerated
     *
     * @param fieldNumbers the index of fields that must be skipped, as a vararg
     */
    public void setSkipFieldNumbers(int... fieldNumbers) {
        skipFields.clear();
        List<Integer> list = Arrays.stream(fieldNumbers).boxed().collect(Collectors.toList());
        skipFields.addAll(list);

        setBitmaps();
    }

    /**
     * Get the index of fields that must be skipped from the bitmap generation and pack processing
     *
     * @return an array of field indexes
     */
    public int[] getSkipFieldNumbers() {
        return skipFields.stream().mapToInt(number -> number).toArray();
    }

    /**
     * Undo the "setSkipFieldNumbers" method operation
     */
    public void clearSkipFields() {
        skipFields.clear();
        setBitmaps();
    }

    /**
     * Get the primary bitmap of this message
     *
     * @return the primary bitmap
     */
    public Bitmap getPrimaryBitmap() {
        return getBitmap(BitmapType.PRIMARY);
    }

    /**
     * Get the secondary bitmap of this message, if available
     *
     * @return the secondary bitmap
     */
    public Bitmap getSecondaryBitmap() {
        return getBitmap(BitmapType.SECONDARY);
    }

    /**
     * Get the tertiary bitmap of this message, if available
     *
     * @return the tertiary bitmap
     */
    public Bitmap getTertiaryBitmap() {
        return getBitmap(BitmapType.TERTIARY);
    }

    /**
     * Check if a field has been defined
     *
     * @param fieldNumber the index of the field
     * @return true if field has been defined
     */
    public boolean hasField(int fieldNumber) {
        return fields.containsKey(fieldNumber);
    }

    /**
     * Get a field from this message, if defined
     *
     * @param fieldNumber the index of the field
     * @return the field object, if defined
     */
    public Field getField(int fieldNumber) {
        return fields.get(fieldNumber);
    }

    /**
     * Check if a field has been defined as a BitmapField
     *
     * @param fieldNumber the index of the field
     * @return true if field type is BitmapField
     */
    public boolean isBitmapField(int fieldNumber) {
        return fields.get(fieldNumber) instanceof BitmapField;
    }

    /**
     * Check if a field has been defined as a SingleField
     *
     * @param fieldNumber the index of the field
     * @return true if field type is SingleField
     */
    public boolean isSingleField(int fieldNumber) {
        return fields.get(fieldNumber) instanceof SingleField;
    }

    /**
     * Check if a field has been defined as a CombineField
     *
     * @param fieldNumber the index of the field
     * @return true if field type is CombineField
     */
    public boolean isCombineField(int fieldNumber) {
        return fields.get(fieldNumber) instanceof CombineField;
    }

    /**
     * Get the lowest index of the fields defined in this message
     *
     * @param doSkipping if true, then only the index of fields that exist in bitmap will be searched.
     * @return the lowest index
     */
    public int getMinimumFieldNumber(boolean doSkipping) {
        int[] fieldNumbers = getFieldNumbers(doSkipping);
        if (fieldNumbers.length > 0) return fieldNumbers[0];
        else return 0;
    }

    /**
     * Get the largest index of the fields defined in this message
     *
     * @param doSkipping if true, then only the index of fields that exist in bitmap will be searched.
     * @return the largest index
     */
    public int getMaximumFieldNumber(boolean doSkipping) {
        int[] fieldNumbers = getFieldNumbers(doSkipping);
        if (fieldNumbers.length > 0) return fieldNumbers[fieldNumbers.length - 1];
        else return 0;
    }

    /**
     * Set the value of a field defined in this message
     * This method is for setting the value of the SingleField types
     *
     * @param fieldNumber the index of the field
     * @param value the field value as a byte array
     * @throws ISO8583Exception If the value is invalid
     */
    public void setValue(int fieldNumber, byte[] value) throws ISO8583Exception {
        if (isValueOK(fieldNumber, value))
            ((SingleField) fields.get(fieldNumber)).setValue(value);
    }

    /**
     * Set the value of a field defined in this message
     * This method is for setting the value of the SingleField types
     *
     * @param fieldNumber the index of the field
     * @param value the field value as a string
     * @throws ISO8583Exception If the value is invalid
     */
    public void setValue(int fieldNumber, String value) throws ISO8583Exception {
        if (isValueOK(fieldNumber, value))
            ((SingleField) fields.get(fieldNumber)).setValue(value, charset);
    }

    /**
     * Set the value of a subfield defined in a combination field
     *
     * @param fieldNumberSequence
     * @param value the field value as a byte array
     * @throws ISO8583Exception If the value or sequence number are invalid
     */
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
