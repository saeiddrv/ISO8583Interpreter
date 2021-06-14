package ir.saeiddrv.iso8583.message.fields.formatters;

import ir.saeiddrv.iso8583.message.utilities.Validator;

public class MaskCardNumber implements ValueFormatter {

    @Override
    public String getFormatted(int fieldNumber, String value) {
        if (Validator.cardNumber(value)) {
            return String.format("%s%s%s",
                    value.substring(0, 6),
                    new String(new char[value.length() - 10]).replace("\0", "*"),
                    value.substring(value.length() - 4));
        } else {
            return value;
        }
    }
}
