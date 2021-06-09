package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.SingleField;
import ir.saeiddrv.iso8583.message.fields.shortcuts.ShortcutField;
import ir.saeiddrv.iso8583.message.interpreters.base.*;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;
import java.nio.charset.Charset;
import java.util.Objects;

public class ISOBuilder {

    private final Message message;

    private ISOBuilder() {
        this(new Message());
    }

    private ISOBuilder(Message message) {
        this.message = message;
    }

    private boolean isFieldNumberOK(int fieldNumber) throws ISOException {
        if (fieldNumber < 0 || fieldNumber > 192)
            throw new ISOException("The field number in ISO8583 cannot be less than '0' or greater than '129'.");
        return true;
    }

    public static ISOBuilder create() {
        return new ISOBuilder();
    }

    public static ISOBuilder from(Message message) {
        return new ISOBuilder(message);
    }

    public ISOBuilder setDescription(String description) {
        message.setDescription(description);
        return this;
    }

    public ISOBuilder setCharset(Charset charset) {
        if (charset != null)
            message.setCharset(charset);
        return this;
    }

    public ISOBuilder setCharset(String charsetName) {
        if (charsetName != null)
            message.setCharset(Charset.forName(charsetName));
        return this;
    }

    public ISOBuilder setMessageLengthInterpreter(int lengthCount,
                                                  MessageLengthInterpreter interpreter) {
        message.setLengthCount(lengthCount);
        message.setLengthInterpreter(
                Objects.requireNonNull(interpreter,
                        "The 'Message Length Interpreter' cannot be set to null."));
        return this;
    }

    public ISOBuilder setHeaderInterpreter(HeaderInterpreter interpreter) {
        message.setHeader(new Header(
                Objects.requireNonNull(interpreter,
                        "The 'Header Interpreter' cannot be set to null.")));
        return this;
    }

    public ISOBuilder setMTI(String mtiLiteral,
                             MTIInterpreter interpreter) throws ISOException {
        if (Validator.mti(mtiLiteral)) {
            int[] mtiArray = TypeUtils.numberStringToIntArray(mtiLiteral);
            message.setMTI(MTI.create(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3],
                    Objects.requireNonNull(interpreter, "The 'MTI Interpreter' cannot be set to null"))
            );
            return this;
        }
        throw new ISOException("[%s] Is an invalid value for ISO8583 MTI, " +
                "The message type indicator is a four-digit numeric field " +
                "which indicates the overall function of the message.", mtiLiteral);
    }

    public ISOBuilder defineField(SingleField singleField) throws ISOException {
        if (singleField != null) {
            if (isFieldNumberOK(singleField.getNumber()))
                message.addField(singleField.getNumber(), singleField);
        }
        return this;
    }

    public ISOBuilder reDefineField(Field field) throws ISOException {
        if (field != null)
            message.replaceField(field.getNumber(), field);
        return this;
    }

    public ISOBuilder defineField(int number, ShortcutField field) throws ISOException {
        if (isFieldNumberOK(number))
            message.addField(number, Objects.requireNonNull(field,
                    "The 'Field'[" + number + "] cannot be set to null").toField(number));
        return this;
    }

    public ISOBuilder reDefineField(int number, ShortcutField field) throws ISOException {
        message.replaceField(number, Objects.requireNonNull(field,
                "The 'Field'[" + number + "] cannot be set to null").toField(number));
        return this;
    }

    public Message buildMessage() throws ISOException {
        message.setBitmaps();
        return message;
    }
}
