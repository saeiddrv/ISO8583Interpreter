package ir.saeiddrv.iso8583.message.interpreters;

import ir.saeiddrv.iso8583.message.Range;
import ir.saeiddrv.iso8583.message.unpacks.UnpackBitmapResult;
import ir.saeiddrv.iso8583.message.fields.Bitmap;
import ir.saeiddrv.iso8583.message.ISO8583Exception;
import ir.saeiddrv.iso8583.message.interpreters.base.BitmapInterpreter;
import ir.saeiddrv.iso8583.message.utilities.TypeUtils;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class BinaryBitmapInterpreter implements BitmapInterpreter {

    @Override
    public String getName() {
        return "Binary Bitmap Interpreter";
    }

    @Override
    public byte[] pack(Bitmap bitmap, Charset charset) throws ISO8583Exception {
        return TypeUtils.encodeBytes(bitmap.getValue(), charset);
    }

    @Override
    public UnpackBitmapResult unpack(byte[] message,
                                     int offset,
                                     int length,
                                     Range range,
                                     Charset charset) throws ISO8583Exception {
        int endOffset = offset + length;
        byte[] pack = Arrays.copyOfRange(message, offset, endOffset);
        pack = TypeUtils.encodeBytes(pack, charset);
        BitSet bitSet = TypeUtils.byteArray2BitSet(pack, length, range.getStart());

        List<Integer> filedNumbers = new ArrayList<>();
        for (int i = 0; i < length * 8; i++)
            if (bitSet.get(i))
                filedNumbers.add(i + range.getStart());

        int[] fieldNumbers = filedNumbers.stream().mapToInt(number -> number).toArray();

        return new UnpackBitmapResult(fieldNumbers, endOffset);
    }
}
