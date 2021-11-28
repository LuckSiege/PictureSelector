package top.zibin.luban.io;

/**
 * @author：luck
 * @date：2021/8/26 3:19 下午
 * @describe：ArrayAdapterInterface
 */
interface ArrayAdapterInterface<T> {

    /**
     * TAG for logging.
     */
    String getTag();

    /**
     * Return the length of the given array.
     */
    int getArrayLength(T array);

    /**
     * Allocate and return an array of the specified size.
     */
    T newArray(int length);

    /**
     * Return the size of an element in the array in bytes (e.g. for int return 4).
     */
    int getElementSizeInBytes();
}
