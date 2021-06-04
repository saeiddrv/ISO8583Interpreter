package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.SingleField;
import ir.saeiddrv.iso8583.message.fields.shortcuts.ShortcutField;
import ir.saeiddrv.iso8583.message.interpreters.base.*;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;
import java.nio.charset.Charset;
import java.util.Objects;

public class ISOMessageBuilder {

    private final ISOMessage message;

    private ISOMessageBuilder() {
        this(new ISOMessage());
    }

    private ISOMessageBuilder(ISOMessage message) {
        this.message = message;
    }

    private boolean isFieldNumberOK(int fieldNumber) throws ISOMessageException {
        if (fieldNumber < 0 || fieldNumber > 192)
            throw new ISOMessageException("The field number in ISO8583 cannot be less than '0' or greater than '129'.");
        return true;
    }

    public static ISOMessageBuilder create() {
        return new ISOMessageBuilder();
    }

    public static ISOMessageBuilder from(ISOMessage message) {
        return new ISOMessageBuilder(message);
    }

    public ISOMessageBuilder setDescription(String description) {
        message.setDescription(description);
        return this;
    }

    public ISOMessageBuilder setCharset(Charset charset) {
        if (charset != null)
            message.setCharset(charset);
        return this;
    }

    public ISOMessageBuilder setCharset(String charsetName) {
        if (charsetName != null)
            message.setCharset(Charset.forName(charsetName));
        return this;
    }

    public ISOMessageBuilder setMessageLengthInterpreter(int lengthCount,
                                                         MessageLengthInterpreter interpreter) {
        message.setLengthCount(lengthCount);
        message.setLengthInterpreter(
                Objects.requireNonNull(interpreter,
                        "The 'Message Length Interpreter' cannot be set to null."));
        return this;
    }

    public ISOMessageBuilder setHeaderInterpreter(HeaderInterpreter interpreter) {
        message.setHeader(new Header(
                Objects.requireNonNull(interpreter,
                        "The 'Header Interpreter' cannot be set to null.")));
        return this;
    }

    public ISOMessageBuilder setMTI(String mtiLiteral,
                                    MTIInterpreter interpreter) throws ISOMessageException {
        if (Validator.mti(mtiLiteral)) {
            int[] mtiArray = TypeUtils.numberLiteralToIntArray(mtiLiteral);
            message.setMTI(MTI.create(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3],
                    Objects.requireNonNull(interpreter, "The 'MTI Interpreter' cannot be set to null"))
            );
            return this;
        }
        throw new ISOMessageException("[%s] Is an invalid value for ISO8583 MTI, " +
                "The message type indicator is a four-digit numeric field " +
                "which indicates the overall function of the message.", mtiLiteral);
    }

    public ISOMessageBuilder defineField(SingleField singleField) throws ISOMessageException {
        if (singleField != null) {
            if (isFieldNumberOK(singleField.getNumber()))
                message.addField(singleField.getNumber(), singleField);
        }
        return this;
    }

    public ISOMessageBuilder reDefineField(Field field) throws ISOMessageException {
        if (field != null)
            message.replaceField(field.getNumber(), field);
        return this;
    }

    public ISOMessageBuilder defineField(int number, ShortcutField field) throws ISOMessageException {
        if (isFieldNumberOK(number))
            message.addField(number, Objects.requireNonNull(field,
                    "The 'Field'[" + number + "] cannot be set to null").toField(number));
        return this;
    }

    public ISOMessageBuilder reDefineField(int number, ShortcutField field) throws ISOMessageException {
        message.replaceField(number, Objects.requireNonNull(field,
                "The 'Field'[" + number + "] cannot be set to null").toField(number));
        return this;
    }

    public ISOMessage build() throws ISOMessageException {
        message.setBitmaps();
        return message;
    }
}
