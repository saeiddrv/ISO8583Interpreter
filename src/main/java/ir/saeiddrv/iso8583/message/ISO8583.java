package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.fields.Field;
import ir.saeiddrv.iso8583.message.fields.SingleField;
import ir.saeiddrv.iso8583.message.fields.shortcuts.ShortcutField;
import ir.saeiddrv.iso8583.message.headers.HeaderContent;
import ir.saeiddrv.iso8583.message.interpreters.base.*;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class ISO8583 {

    private final Message message;

    private ISO8583() {
        this(new Message());
    }

    private ISO8583(Message message) {
        this.message = message;
    }

    private boolean isFieldNumberOK(int fieldNumber) throws ISO8583Exception {
        if (fieldNumber < 0 || fieldNumber > 192)
            throw new ISO8583Exception("The field number in ISO8583 cannot be less than '0' or greater than '129'.");
        return true;
    }

    public static ISO8583 create() {
        return new ISO8583();
    }

    public static ISO8583 from(Message message) {
        return new ISO8583( Objects.requireNonNull(message,
                "The 'Message' cannot be set to null."));
    }

    public ISO8583 setDescription(String description) {
        message.setDescription(description);
        return this;
    }

    public ISO8583 setCharset(Charset charset) {
        if (charset != null)
            message.setCharset(charset);
        return this;
    }

    public ISO8583 setCharset(String charsetName) {
        if (charsetName != null)
            message.setCharset(Charset.forName(charsetName));
        return this;
    }

    public ISO8583 setMessageLengthInterpreter(int lengthCount,
                                               MessageLengthInterpreter interpreter) {
        message.setLengthCount(lengthCount);
        message.setLengthInterpreter(
                Objects.requireNonNull(interpreter,
                        "The 'Message Length Interpreter' cannot be set to null."));
        return this;
    }

    public ISO8583 setHeader(HeaderContent content, HeaderInterpreter interpreter) {
        Header header = new Header(
                Objects.requireNonNull(content, "The 'HeaderContent' cannot be set to null."),
                Objects.requireNonNull(interpreter, "The 'Header Interpreter' cannot be set to null."));
        header.getContent().setCharset(message.getCharset());
        message.setHeader(header);
        return this;
    }

    public ISO8583 setMTI(String mtiLiteral,
                          MTIInterpreter interpreter) throws ISO8583Exception {
        if (Validator.mti(mtiLiteral)) {
            int[] mtiArray = TypeUtils.numberStringToIntArray(mtiLiteral);
            message.setMTI(new MTI(mtiArray[0], mtiArray[1], mtiArray[2], mtiArray[3],
                    Objects.requireNonNull(interpreter, "The 'MTI Interpreter' cannot be set to null"))
            );
            return this;
        }
        throw new ISO8583Exception("[%s] Is an invalid value for ISO8583 MTI, " +
                "The message type indicator is a four-digit numeric field " +
                "which indicates the overall function of the message.", mtiLiteral);
    }

    public ISO8583 defineField(SingleField singleField) throws ISO8583Exception {
        if (singleField != null) {
            if (isFieldNumberOK(singleField.getNumber()))
                message.addField(singleField.getNumber(), singleField);
        }
        return this;
    }

    public ISO8583 reDefineField(Field field) throws ISO8583Exception {
        if (field != null)
            message.replaceField(field.getNumber(), field);
        return this;
    }

    public ISO8583 defineField(int number, ShortcutField field) throws ISO8583Exception {
        if (isFieldNumberOK(number))
            message.addField(number, Objects.requireNonNull(field,
                    "The Field[" + number + "] cannot be set to null").toField(number));
        return this;
    }

    public ISO8583 reDefineField(int number, ShortcutField field) throws ISO8583Exception {
        message.replaceField(number, Objects.requireNonNull(field,
                "The Field[" + number + "] cannot be set to null").toField(number));
        return this;
    }

    public Message buildMessage() throws ISO8583Exception {
        message.setBitmaps();
        return message;
    }

    public Message unpackMessage(byte[] packMessage) throws ISO8583Exception {
        message.clearAllValue(false);
        return message.unpack(packMessage);
    }

    public Message unpackMessage(byte[] packMessage, PrintStream printStream) throws ISO8583Exception {
        message.clearAllValue(false);
        return message.unpack(packMessage, printStream);
    }
}
