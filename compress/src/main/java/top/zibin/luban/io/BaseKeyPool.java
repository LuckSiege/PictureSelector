package top.zibin.luban.io;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author：luck
 * @date：2021/8/26 3:13 下午
 * @describe：BaseKeyPool
 */
abstract class BaseKeyPool<T extends PoolAble> {
    private static final int MAX_SIZE = 20;
    private final Queue<T> keyPool = createQueue(MAX_SIZE);

    T get() {
        T result = keyPool.poll();
        if (result == null) {
            result = create();
        }
        return result;
    }

    public void offer(T key) {
        if (keyPool.size() < MAX_SIZE) {
            keyPool.offer(key);
        }
    }

    public static <T> Queue<T> createQueue(int size) {
        return new ArrayDeque<>(size);
    }

    abstract T create();
}
