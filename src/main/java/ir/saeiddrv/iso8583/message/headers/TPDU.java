package ir.saeiddrv.iso8583.message.headers;

import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

public class TPDU implements HeaderContent {

    private final byte protocolID;
    private final byte[] sourceAddress;
    private final byte[] destinationAddress;
    private Charset charset;

    private TPDU(byte protocolID, byte[] sourceAddress, byte[] destinationAddress) {
        this.protocolID = protocolID;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }

    public static TPDU fromBytes(byte protocolID, byte[] sourceAddress, byte[] destinationAddress) {
        if (sourceAddress == null || sourceAddress.length != 2)
            throw new IllegalArgumentException("Invalid sourceAddress (Must be two bytes): "
                    + Objects.requireNonNull(sourceAddress).length);
        if (destinationAddress == null || destinationAddress.length != 2)
            throw new IllegalArgumentException("Invalid destinationAddress (Must be two bytes): "
                    + Objects.requireNonNull(destinationAddress).length);

        return new TPDU(protocolID, sourceAddress, destinationAddress);
    }

    public static TPDU fromInteger(int protocolID, int sourceAddress, int destinationAddress) {
        // int -> byte[]
        byte[] pIBytes = TypeUtils.intToByte(protocolID);
        byte[] sABytes = TypeUtils.intToByte(sourceAddress);
        byte[] dABytes = TypeUtils.intToByte(destinationAddress);

        if (pIBytes.length != 1)
            throw new IllegalArgumentException("Invalid protocolID (Must be one byte number): "
                    + protocolID);
        if (sABytes.length < 1 || sABytes.length > 2)
            throw new IllegalArgumentException("Invalid sourceAddress (Must be one or two bytes number): "
                    + sourceAddress);
        if (dABytes.length < 1 || dABytes.length > 2)
            throw new IllegalArgumentException("Invalid destinationAddress (Must be one or two bytes number): "
                    + destinationAddress);

        // pad-left by zero (if necessary)
        if (sABytes.length == 1) sABytes = new byte[]{0x00, sABytes[0]};
        if (dABytes.length == 1) dABytes = new byte[]{0x00, dABytes[0]};

        return new TPDU(pIBytes[0], sABytes, dABytes);
    }

    public static TPDU fromHexString(String protocolID, String sourceAddress, String destinationAddress) {
        if (!Validator.hex(protocolID, 1, 2))
            throw new IllegalArgumentException("Invalid protocolID (Must be one or two hex characters): "
                    + protocolID);
        if (!Validator.hex(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid sourceAddress (Must be one..four hex characters): "
                    + sourceAddress);
        if (!Validator.hex(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid destinationAddress (Must be one..four hex characters): "
                    + destinationAddress);

        // pad-left by zero (if necessary)
        protocolID = PadUtils.padLeft(protocolID, 2, '0');
        sourceAddress = PadUtils.padLeft(sourceAddress, 4, '0');
        destinationAddress = PadUtils.padLeft(destinationAddress, 4, '0');

        // HEX String -> byte[]
        return new TPDU(
                TypeUtils.hexStringToByte(protocolID),
                TypeUtils.hexStringToByteArray(sourceAddress),
                TypeUtils.hexStringToByteArray(destinationAddress));
    }

    public static TPDU fromDecimal(String protocolID, String sourceAddress, String destinationAddress) {
        if (!Validator.number(protocolID, 1, 2))
            throw new IllegalArgumentException("Invalid protocolID (Must be one..two decimal numbers): "
                    + protocolID);
        if (!Validator.number(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid sourceAddress (Must be one..four decimal numbers): "
                    + sourceAddress);
        if (!Validator.number(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid destinationAddress (Must be one..four decimal numbers): "
                    + destinationAddress);

        // pad-left by zero (if necessary)
        protocolID = PadUtils.padLeft(protocolID, 2, '0');
        sourceAddress = PadUtils.padLeft(sourceAddress, 4, '0');
        destinationAddress = PadUtils.padLeft(destinationAddress, 4, '0');

        // String -> byte[] -> BCD byte[]
        return new TPDU(
                TypeUtils.byteArrayToBCD(TypeUtils.charSequenceToByteArray(protocolID))[0],
                TypeUtils.byteArrayToBCD(TypeUtils.charSequenceToByteArray(sourceAddress)),
                TypeUtils.byteArrayToBCD(TypeUtils.charSequenceToByteArray(destinationAddress)));
    }

    public byte getProtocolID() {
        return protocolID;
    }

    public byte[] getSourceAddress() {
        return sourceAddress;
    }

    public byte[] getDestinationAddress() {
        return destinationAddress;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] getValue() {
        // Creating 5 bytes
        byte[] value = ByteBuffer.allocate(5)
                .put(getProtocolID())          // 1 byte
                .put(getSourceAddress())       // 2 bytes
                .put(getDestinationAddress())  // 2 bytes
                .array();
        return TypeUtils.encodeBytes(value, charset);
    }

    @Override
    public String getValueAsString() {
        return TypeUtils.byteArrayToHexString(getValue());
    }

    @Override
    public String toString() {
        String value = getValueAsString();
        return String.format("@TPDU[protocolID: %s, sourceAddress: %s, destinationAddress: %s]",
                value.substring(0, 2),
                value.substring(2, 6),
                value.substring(6, 10));
    }
}
