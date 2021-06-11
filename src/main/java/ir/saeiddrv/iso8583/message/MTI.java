package ir.saeiddrv.iso8583.message;

import ir.saeiddrv.iso8583.message.interpreters.base.MTIInterpreter;
import ir.saeiddrv.iso8583.message.unpacks.UnpackMTIResult;
import java.nio.charset.Charset;
import java.util.Locale;

public class MTI {

    private final MTIInterpreter interpreter;
    private int[] value;

    static MTI create(int isoVersion, int messageClass, int messageFunction, int messageOrigin, MTIInterpreter interpreter) {
        return new MTI(isoVersion, messageClass, messageFunction, messageOrigin, interpreter);
    }

    private MTI(int isoVersion, int messageClass, int messageFunction, int messageOrigin, MTIInterpreter interpreter) {
        this.value = new int[4];
        this.value[0] = isoVersion;
        this.value[1] = messageClass;
        this.value[2] = messageFunction;
        this.value[3] = messageOrigin;
        this.interpreter = interpreter;
    }

    MTIInterpreter getInterpreter() {
        return interpreter;
    }

    void clear() {
        this.value = new int[0];
    }

    public boolean hasValue() {
        return value.length == 4;
    }

    public int getIsoVersion() {
        return hasValue() ? value[0] : -1;
    }

    public String getIsoVersionDescription() {
        switch (getIsoVersion()){
            case 0: return "ISO 8583:1987";
            case 1: return "ISO 8583:1993";
            case 2: return "ISO 8583:2003";
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: return "Reserved by ISO";
            case 8: return "National Use";
            case 9: return "Private Use";
            default: return "UNDEFINED";
        }
    }

    public int getMessageClass() {
        return hasValue() ? value[1] : -1;
    }

    public String getMessageClassDescription() {
        switch (getMessageClass()){
            case 0:
            case 9: return "Reserved by ISO";
            case 1: return "Authorization";
            case 2: return "Financial";
            case 3: return "File Action";
            case 4: return "Reversal and Chargeback";
            case 5: return "Reconciliation";
            case 6: return "Administrative";
            case 7: return "Fee Collection";
            case 8: return "Network Management";
            default: return "UNDEFINED";
        }
    }

    public int getMessageFunction() {
        return hasValue() ? value[2] : -1;
    }

    public String getMessageFunctionDescription() {
        switch (getMessageFunction()){
            case 0: return "Request";
            case 1: return "Request, Response";
            case 2: return "Advice";
            case 3: return "Advice, Response";
            case 4: return "Notification";
            case 5: return "Notification Acknowledgement";
            case 6: return "Instruction";
            case 7: return "Instruction Acknowledgement";
            case 8:
            case 9: return "Reserved by ISO";
            default: return "UNDEFINED";
        }
    }

    public int getMessageOrigin() {
        return hasValue() ? value[3] : -1;
    }

    public String getMessageOriginDescription() {
        switch (getMessageOrigin()){
            case 0: return "Acquirer";
            case 1: return "Acquirer, Repeat";
            case 2: return "Issuer";
            case 3: return "Issuer, Repeat";
            case 4: return "Other";
            case 5: return "Other, Repeat";
            case 6:
            case 7:
            case 8:
            case 9: return "Reserved by ISO";
            default: return "UNDEFINED";
        }
    }

    public String getLiteral() {
        return String.format(Locale.ENGLISH, "%d%d%d%d",
                getIsoVersion(), getMessageClass(), getMessageFunction(), getMessageOrigin());
    }

    public String getLiteralDescription() {
        return String.format(Locale.ENGLISH, "%d(%s) %d(%s) %d(%s) %d(%s)",
                getIsoVersion(), getIsoVersionDescription(),
                getMessageClass(), getMessageClassDescription(),
                getMessageFunction(), getMessageFunctionDescription(),
                getMessageOrigin(), getMessageOriginDescription());
    }

    public byte[] pack(Charset charset) throws ISO8583Exception {
        return hasValue() ? interpreter.pack(getLiteral(), charset) : new byte[0];
    }

    public UnpackMTIResult unpack(byte[] message, int offset, Charset charset) throws ISO8583Exception {
        return interpreter.unpack(message, offset, charset);
    }

    @Override
    public String toString() {
        return String.format("@MTI[value: %s [%s], interpreter: %s]",
                getLiteral(), getLiteralDescription(), interpreter.getName());
    }
}
