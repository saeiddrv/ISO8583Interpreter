package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.*;
import ir.saeiddrv.iso8583.message.fields.formatters.ValueFormatter;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.unpacks.UnpackLengthResult;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class SingleField implements Field {

    private final int number;
    private final Length length;
    private final Content content;
    private Charset charset = null;
    private ValueFormatter formatter = null;
    private String description = "UNDEFINED";

    public static SingleField createRaw(int number) {
        return  new SingleField(number,
                null,
                LengthValue.UNDEFINED,
                null,
                ContentPad.NO_PADDING);
    }

    public static SingleField createFixed(int number,
                                          int maximumLength,
                                          ContentInterpreter contentInterpreter,
                                          ContentPad contentPad) {
        return  new SingleField(number,
                null,
                LengthValue.create(0, maximumLength),
                Objects.requireNonNull(contentInterpreter,
                        "ContentInterpreter of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(contentPad,
                        "ContentPad of FIELD[" + number + "] must not be null"));
    }

    public static SingleField create(int number,
                                     LengthInterpreter lengthInterpreter,
                                     LengthValue lengthValue,
                                     ContentInterpreter contentInterpreter,
                                     ContentPad contentPad) {
        return  new SingleField(number,
                Objects.requireNonNull(lengthInterpreter,
                        "LengthInterpreter of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(lengthValue,
                        "LengthValue of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(contentInterpreter,
                        "ContentInterpreter of FIELD[" + number + "] must not be null"),
                Objects.requireNonNull(contentPad,
                        "ContentPad of FIELD[" + number + "] must not be null"));
    }

    private SingleField(int number,
                        LengthInterpreter lengthInterpreter,
                        LengthValue lengthValue,
                        ContentInterpreter contentInterpreter,
                        ContentPad contentPad) {
        this.number = number;
        this.length = new Length(lengthValue, lengthInterpreter);
        this.content = new Content(contentInterpreter, contentPad);
    }

    public Length getLength() {
        return length;
    }

    public Content getContent() {
        return content;
    }

    public boolean hasLength() {
        return length.isDefined() && !length.isFixed();
    }

    public void setValue(byte[] value) {
        content.setValue(value);
    }

    public void setValue(String value, Charset charset) {
        content.setValue(value, charset);
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
        if (this.charset == null) this.charset = charset;
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
        this.description = description;
    }

    @Override
    public byte[] getValue() {
        return content.getValue();
    }

    @Override
    public String getValueAsString() {
        return content.getValueAsString(charset);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void clear() {
        setValue(new byte[0]);
    }

    @Override
    public byte[] pack() throws ISO8583Exception {
        try {
            // PREPARE CONTENT VALUE
            if (length.isFixed())
                content.doPad(length.getMaximumValue());

            // START FIELD PACKING, CREATE PACK BUFFER
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            buffer.write(length.pack(number, content.getValue().length, charset));
            buffer.write(content.pack(number, length.getValue(), charset));

            // FINISHED
            return buffer.toByteArray();

        } catch (Exception exception) {
            throw new ISO8583Exception("FIELD[%s]: %s", number, exception.getMessage());
        }
    }

    @Override
    public int unpack(byte[] message, int offset) throws ISO8583Exception {
        try {
            // UNPACK LENGTH
            int messageLength = 0;
            if (length.isFixed()) {
                messageLength = length.getMaximumValue();
            } else if (length.hasInterpreter()) {
                UnpackLengthResult unpackLength = length.unpack(message, offset, number, charset);
                if (unpackLength != null) {
                    messageLength = unpackLength.getValue();
                    offset = unpackLength.getNextOffset();
                }
            }

            // UNPACK CONTENT
            UnpackContentResult unpackContent = content.unpack(message, offset, number, messageLength, charset);
            setValue(unpackContent.getValue());

            // FINISHED
            return unpackContent.getNextOffset();

        }  catch (Exception exception) {
            exception.printStackTrace();
            throw new ISO8583Exception("FIELD[%s]: %s", number, exception.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("@SingleField[number: %s, value: %s, length: %s, content: %s, charset: %s, description: %s]",
                number,
                hasFormatter() ? getValueFormatted() : getValueAsString(),
                length,
                content,
                charset.displayName(),
                description);
    }
}
