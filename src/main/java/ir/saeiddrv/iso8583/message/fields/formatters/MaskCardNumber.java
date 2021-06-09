package ir.saeiddrv.iso8583.message.fields.formatters;

import ir.saeiddrv.iso8583.message.utilities.Validator;

public class MaskCardNumber implements ValueFormatter {

    @Override
    public String getFormatted(int fieldNumber, String... value) {
        if (value.length > 0 && Validator.cardNumber(value[0])) {
            String cardNumber = value[0];
            return String.format("%s%s%s",
                    cardNumber.substring(0, 6),
                    new String(new char[cardNumber.length() - 10]).replace("\0", "*"),
                    cardNumber.substring(cardNumber.length() - 4));
        } else {
            return "";
        }
    }
}
