package top.zibin.luban.io;

/**
 * @author：luck
 * @date：2021/8/26 3:15 下午
 * @describe：ArrayPool
 */
public interface ArrayPool {

    /**
     * Optionally adds the given array of the given type to the pool.
     *
     * <p>Arrays may be ignored, for example if the array is larger than the maximum size of the pool.
     *
     * @deprecated Use {@link #put(Object)}
     */
    @Deprecated
    <T> void put(T array, Class<T> arrayClass);

    /**
     * Optionally adds the given array of the given type to the pool.
     *
     * <p>Arrays may be ignored, for example if the array is larger than the maximum size of the pool.
     */
    <T> void put(T array);

    /**
     * Returns a non-null array of the given type with a length >= to the given size.
     *
     * <p>If an array of the given size isn't in the pool, a new one will be allocated.
     *
     * <p>This class makes no guarantees about the contents of the returned array.
     *
     * @see #getExact(int, Class)
     */
    <T> T get(int size, Class<T> arrayClass);


    /** Clears all arrays from the pool. */
    void clearMemory();

}
