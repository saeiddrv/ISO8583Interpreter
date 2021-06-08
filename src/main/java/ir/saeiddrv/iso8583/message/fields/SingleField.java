package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.*;
import ir.saeiddrv.iso8583.message.fields.formatters.FieldFormatter;
import ir.saeiddrv.iso8583.message.interpreters.base.ContentInterpreter;
import ir.saeiddrv.iso8583.message.interpreters.base.LengthInterpreter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public class SingleField implements Field {

    private final int number;
    private final Length length;
    private final Content content;
    private FieldFormatter formatter = null;
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

    public void setValue(String value) {
        content.setValue(value);
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
        return content.getValueAsString();
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
    public byte[] pack(Charset charset) throws IOException, ISOMessageException {
        // PREPARE CONTENT VALUE
        if (length.isFixed())
            content.doPad(length.getMaximumValue());

        // START FIELD PACKING, CREATE PACK BUFFER
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(length.pack(number, content.getValue().length, charset));
        buffer.write(content.pack(number, length.getValue(), charset));

        // FINISHED
        return buffer.toByteArray();
    }

    @Override
    public String toString() {
        return String.format("@SingleField[number: %s, value: %s, length: %s, content: %s, description: %s]",
                number, hasFormatter() ? getFormatted() : getValue(), length, content, description);
    }
}
