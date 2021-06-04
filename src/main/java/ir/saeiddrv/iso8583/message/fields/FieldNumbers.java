package ir.saeiddrv.iso8583.message.fields;

public final class FieldNumbers {
    private FieldNumbers() {}

    // Bit Map, Primary
    public final static int BITMAP_PRIMARY = 0;

    // Bit Map, Secondary
    public final static int BITMAP_SECONDARY = 1;

    // Primary Account Number (PAN)
    public final static int PRIMARY_ACCOUNT_NUMBER = 2;

    // Processing Code
    public final static int PROCESSING_CODE = 3;

    // Amount, Transaction
    public final static int AMOUNT_TRANSACTION = 4;

    // Amount, Settlement
    public final static int AMOUNT_SETTLEMENT = 5;

    // Amount, Cardholder Billing
    public final static int AMOUNT_CARDHOLDER_BILLING = 6;

    // TRANSMISSION DATE AND TIME
    public final static int TRANSMISSION_DATE_TIME = 7;





    public final static int TERTIARY_BITMAP = 65;

}
