package top.zibin.luban.io;

/**
 * @author：luck
 * @date：2021/8/26 3:20 下午
 * @describe：ByteArrayAdapter
 */
public final class ByteArrayAdapter implements ArrayAdapterInterface<byte[]> {
    private static final String TAG = "ByteArrayPool";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public int getArrayLength(byte[] array) {
        return array.length;
    }

    @Override
    public byte[] newArray(int length) {
        return new byte[length];
    }

    @Override
    public int getElementSizeInBytes() {
        return 1;
    }
}
