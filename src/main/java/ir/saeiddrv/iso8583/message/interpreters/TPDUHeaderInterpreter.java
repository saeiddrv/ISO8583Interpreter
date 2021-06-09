package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.interpreters.base.HeaderInterpreter;
import ir.saeiddrv.iso8583.message.utilities.PadUtils;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import ir.saeiddrv.iso8583.message.utilities.Validator;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

public class TPDUHeaderInterpreter implements HeaderInterpreter {

    private final byte protocolID;
    private final byte[] sourceAddress;
    private final byte[] destinationAddress;

    private TPDUHeaderInterpreter(byte protocolID, byte[] sourceAddress, byte[] destinationAddress) {
        this.protocolID = protocolID;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }

    public static TPDUHeaderInterpreter formBytes(byte protocolID, byte[] sourceAddress, byte[] destinationAddress) {
        if (sourceAddress == null || sourceAddress.length != 2)
            throw new IllegalArgumentException("Invalid sourceAddress (Must be two bytes): "
                    + Objects.requireNonNull(sourceAddress).length);
        if (destinationAddress == null || destinationAddress.length != 2)
            throw new IllegalArgumentException("Invalid destinationAddress (Must be two bytes): "
                    + Objects.requireNonNull(destinationAddress).length);

        return new TPDUHeaderInterpreter(protocolID, sourceAddress, destinationAddress);
    }

    public static TPDUHeaderInterpreter formHex(String protocolID, String sourceAddress, String destinationAddress) {
        if (!Validator.hex(protocolID, 1, 2))
            throw new IllegalArgumentException("Invalid protocolID (Must be one to two hex characters): "
                    + protocolID);
        if (!Validator.hex(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid sourceAddress (Must be one to four hex characters): "
                    + sourceAddress);
        if (!Validator.hex(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid destinationAddress (Must be one to four hex characters): "
                    + destinationAddress);

        protocolID = PadUtils.padLeft(protocolID, 2, '0');
        sourceAddress = PadUtils.padLeft(sourceAddress, 4, '0');
        destinationAddress = PadUtils.padLeft(destinationAddress, 4, '0');

        return new TPDUHeaderInterpreter(TypeUtils.hexStringToByte(protocolID),
                TypeUtils.hexStringToByteArray(sourceAddress),
                TypeUtils.hexStringToByteArray(destinationAddress));
    }

    public static TPDUHeaderInterpreter formDecimal(String protocolID, String sourceAddress, String destinationAddress) {
        if (!Validator.number(protocolID, 1, 2))
            throw new IllegalArgumentException("Invalid protocolID (Must be one to two decimal numbers): "
                    + protocolID);
        if (!Validator.number(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid sourceAddress (Must be one to four decimal numbers): "
                    + sourceAddress);
        if (!Validator.number(sourceAddress, 1, 4))
            throw new IllegalArgumentException("Invalid destinationAddress (Must be one to four decimal numbers): "
                    + destinationAddress);

        protocolID = PadUtils.padLeft(protocolID, 2, '0');
        sourceAddress = PadUtils.padLeft(sourceAddress, 4, '0');
        destinationAddress = PadUtils.padLeft(destinationAddress, 4, '0');

        return new TPDUHeaderInterpreter(
                TypeUtils.textToBCDBytes(protocolID)[0],
                TypeUtils.textToBCDBytes(sourceAddress),
                TypeUtils.textToBCDBytes(destinationAddress));
    }

    @Override
    public String getName() {
        return "TPDU Header Interpreter";
    }

    @Override
    public byte[] pack(Charset charset) {
        byte[] pack = ByteBuffer.allocate(5)
                .put(protocolID)
                .put(sourceAddress)
                .put(destinationAddress)
                .array();
        return TypeUtils.encodeBytes(pack, charset);
    }
}
