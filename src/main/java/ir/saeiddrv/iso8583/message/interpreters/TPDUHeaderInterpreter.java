package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.unpacks.UnpackContentResult;
import ir.saeiddrv.iso8583.message.interpreters.base.HeaderInterpreter;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public class TPDUHeaderInterpreter implements HeaderInterpreter {

    private final byte protocolID;
    private final byte[] sourceAddress;
    private final byte[] destinationAddress;

    private TPDUHeaderInterpreter(byte protocolID,
                                  byte[] sourceAddress,
                                  byte[] destinationAddress) {
        this.protocolID = protocolID;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }

    public static TPDUHeaderInterpreter fromBytes(byte protocolID,
                                                  byte[] sourceAddress,
                                                  byte[] destinationAddress) {
        if (sourceAddress == null || sourceAddress.length != 2)
            throw new IllegalArgumentException("Invalid sourceAddress (Must be two bytes): "
                    + Objects.requireNonNull(sourceAddress).length);
        if (destinationAddress == null || destinationAddress.length != 2)
            throw new IllegalArgumentException("Invalid destinationAddress (Must be two bytes): "
                    + Objects.requireNonNull(destinationAddress).length);

        return new TPDUHeaderInterpreter(protocolID, sourceAddress, destinationAddress);
    }

    public static TPDUHeaderInterpreter fromInteger(int protocolID,
                                                    int sourceAddress,
                                                    int destinationAddress) {
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

        return new TPDUHeaderInterpreter(pIBytes[0], sABytes, dABytes);
    }

    public static TPDUHeaderInterpreter fromHexString(String protocolID,
                                                      String sourceAddress,
                                                      String destinationAddress) {
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
        return new TPDUHeaderInterpreter(
                TypeUtils.hexStringToByte(protocolID),
                TypeUtils.hexStringToByteArray(sourceAddress),
                TypeUtils.hexStringToByteArray(destinationAddress));
    }

    public static TPDUHeaderInterpreter fromDecimal(String protocolID,
                                                    String sourceAddress,
                                                    String destinationAddress) {
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
        return new TPDUHeaderInterpreter(
                TypeUtils.byteArrayToBCD(TypeUtils.charSequenceToByteArray(protocolID))[0],
                TypeUtils.byteArrayToBCD(TypeUtils.charSequenceToByteArray(sourceAddress)),
                TypeUtils.byteArrayToBCD(TypeUtils.charSequenceToByteArray(destinationAddress)));
    }

    public int getProtocolID() {
        return TypeUtils.byteArrayToInt(new byte[]{protocolID});
    }

    public int getSourceAddress() {
        return TypeUtils.byteArrayToInt(sourceAddress);
    }

    public int getDestinationAddress() {
        return TypeUtils.byteArrayToInt(destinationAddress);
    }

    @Override
    public String getName() {
        return "TPDU Header Interpreter";
    }

    @Override
    public byte[] getValue() {
        // Creating 5 bytes
        return ByteBuffer.allocate(5)
                .put(protocolID)          // 1 byte
                .put(sourceAddress)       // 2 bytes
                .put(destinationAddress)  // 2 bytes
                .array();
    }

    @Override
    public String getValueAsString() {
        return TypeUtils.byteArrayToHexString(getValue());
    }

    @Override
    public byte[] pack(Charset charset) throws ISO8583Exception {
        byte[] data = getValue();

        // Encoding data with charset
        return TypeUtils.encodeBytes(data, charset);
    }

    @Override
    public UnpackContentResult unpack(byte[] message,
                                      int offset,
                                      Charset charset) throws ISO8583Exception {
        // Finding the latest data position
        int endOffset = offset + 5;

        if (message.length < endOffset)
            throw new ISO8583Exception("UNPACKING ERROR, HEADER(TPDU): The received message length is less than the required amount. " +
                    "[messageLength: %s]: [startIndex: %s, endIndex: %s]", message.length, offset, endOffset);

        // Copying the data related to this unit and encode it with charset
        byte[] data = Arrays.copyOfRange(message, offset, endOffset);
        data = TypeUtils.encodeBytes(data, charset);

        // Creating result object
        return new UnpackContentResult(data, endOffset);
    }
}
