package top.zibin.luban.io;

/**
 * @author：luck
 * @date：2021/8/26 3:21 下午
 * @describe：IntegerArrayAdapter
 */
public final class IntegerArrayAdapter implements ArrayAdapterInterface<int[]> {
    private static final String TAG = "IntegerArrayPool";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public int getArrayLength(int[] array) {
        return array.length;
    }

    @Override
    public int[] newArray(int length) {
        return new int[length];
    }

    @Override
    public int getElementSizeInBytes() {
        return 4;
    }
}

