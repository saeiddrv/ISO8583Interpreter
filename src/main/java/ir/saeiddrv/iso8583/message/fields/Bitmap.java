package ir.saeiddrv.iso8583.message.fields;

import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

public class Bitmap {

    private int[] filedNumbers = new int[0];
    private final BitmapType type;
    private final Range range;
    private final int length;

    Bitmap(BitmapType type, Range range, int length) {
        this.type = type;
        this.range = range;
        this.length = length;
    }

    Bitmap(int[] filedNumbers, BitmapType type, Range range, int length) {
        this.filedNumbers = filedNumbers;
        this.type = type;
        this.range = range;
        this.length = length;
    }

    public void setFiledNumbers(int[] filedNumbers) {
        this.filedNumbers = filedNumbers;
    }

    public boolean isContainsFieldNumber(int fieldNumber) {
        return IntStream.of(filedNumbers).anyMatch(number -> number == fieldNumber);
    }

    public int[] getFiledNumbers() {
        return filedNumbers;
    }

    public BitmapType getType() {
        return type;
    }

    public int getLength() {
        return length;
    }
    
    public int getBitSize() {
        return length * 8;
    }

    public Range getRange() {
        return range;
    }

    public BitSet getBitSet() {
        int size = getBitSize();
        BitSet bitSet = new BitSet(size);
        for (int i = 0; i < size; i++)
            if (isContainsFieldNumber(i + range.getStart())) bitSet.set(i);
        return bitSet;
    }

    public byte[] getValue() {
       return new BigInteger(getValueAsBinaryString(), 2).toByteArray();
    }

    public String getValueAsBinaryString() {
        return TypeUtils.bitSetToBinaryString(getBitSet());
    }

    public String getValueAsHexString() {
        return new BigInteger(getValueAsBinaryString(), 2).toString(16);
    }

    @Override
    public String toString() {
        return String.format("@Bitmap[filedNumbers: %s, type: %s, range: %s, length: %s]",
                Arrays.toString(filedNumbers), type, range, length);
    }
}
