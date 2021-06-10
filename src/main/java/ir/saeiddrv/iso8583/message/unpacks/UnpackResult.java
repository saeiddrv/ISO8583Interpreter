package ir.saeiddrv.iso8583.message.unpacks;

public interface UnpackResult<T> {

    public T getValue();

    public int getNextOffset();

}
